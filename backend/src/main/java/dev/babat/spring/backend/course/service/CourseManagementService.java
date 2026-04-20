package dev.babat.spring.backend.course.service;

import dev.babat.spring.backend.course.dto.CourseManagementDTO;
import dev.babat.spring.backend.course.dto.CreateCourseRequest;
import dev.babat.spring.backend.course.dto.UpdateCourseRequest;
import dev.babat.spring.backend.course.entity.CourseEntity;
import dev.babat.spring.backend.course.entity.CourseImageEntity;
import dev.babat.spring.backend.course.entity.CourseStatus;
import dev.babat.spring.backend.course.repository.CategoryRepository;
import dev.babat.spring.backend.course.repository.CourseImageRepository;
import dev.babat.spring.backend.course.repository.CourseRepository;
import dev.babat.spring.backend.provider.entity.ProviderEntity;
import dev.babat.spring.backend.provider.repository.ProviderRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseManagementService {

    private final CourseRepository courseRepository;
    private final CourseImageRepository courseImageRepository;
    private final CategoryRepository categoryRepository;
    private final ProviderRepository providerRepository;
    private final SlotGeneratorService slotGeneratorService;

    private static final GeometryFactory GEOMETRY_FACTORY =
            new GeometryFactory(new PrecisionModel(), 4326);

    @Transactional
    public CourseManagementDTO createCourse(CreateCourseRequest request, UUID providerId) {
        ProviderEntity provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Provider not found"));

        var category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        CourseEntity course = new CourseEntity();
        course.setProvider(provider);
        course.setCategory(category);
        course.setName(request.name());
        course.setDescription(request.description());
        course.setAddress(request.address());
        course.setCity(request.city());
        course.setPostcode(request.postcode());
        course.setCountry(request.country());
        course.setCapacityPerSlot(request.capacityPerSlot());
        course.setSlotDurationMinutes(request.slotDurationMinutes());
        course.setScheduleConfig(request.scheduleConfig());
        course.setStartDate(request.startDate());
        course.setEndDate(request.endDate());
        course.setStatus(CourseStatus.DRAFT);
        course.setLocation(createPoint(request.lng(), request.lat()));

        CourseEntity saved = courseRepository.save(course);
        return toManagementDto(saved);
    }

    @Transactional
    public CourseManagementDTO updateCourse(UUID courseId, UpdateCourseRequest request, UUID providerId) {
        CourseEntity course = courseRepository.findByIdAndProviderId(courseId, providerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

        if (course.getStatus() == CourseStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot edit a cancelled course");
        }

        boolean scheduleChanged = request.scheduleConfig() != null
                || request.startDate() != null
                || request.endDate() != null
                || request.slotDurationMinutes() != null;

        if (scheduleChanged && course.getStatus() == CourseStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot change schedule of an active course directly — cancel and recreate instead");
        }

        if (request.name() != null) course.setName(request.name());
        if (request.description() != null) course.setDescription(request.description());
        if (request.address() != null) course.setAddress(request.address());
        if (request.city() != null) course.setCity(request.city());
        if (request.postcode() != null) course.setPostcode(request.postcode());
        if (request.country() != null) course.setCountry(request.country());
        if (request.categoryId() != null) {
            var category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
            course.setCategory(category);
        }
        if (request.capacityPerSlot() != null) course.setCapacityPerSlot(request.capacityPerSlot());
        if (request.slotDurationMinutes() != null) course.setSlotDurationMinutes(request.slotDurationMinutes());
        if (request.scheduleConfig() != null) course.setScheduleConfig(request.scheduleConfig());
        if (request.startDate() != null) course.setStartDate(request.startDate());
        if (request.endDate() != null) course.setEndDate(request.endDate());
        if (request.lat() != null && request.lng() != null) {
            course.setLocation(createPoint(request.lng(), request.lat()));
        }

        CourseEntity saved = courseRepository.save(course);
        return toManagementDto(saved);
    }


    @Transactional
    public void publishCourse(UUID courseId, UUID providerId) {
        CourseEntity course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

        if (!course.getProvider().getId().equals(providerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your course");
        }

        if (course.getStatus() != CourseStatus.DRAFT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course is already published");
        }

        course.setStatus(CourseStatus.ACTIVE);
        courseRepository.save(course);
        slotGeneratorService.generateSlots(course);
    }

    @Transactional
    public void cancelCourse(UUID courseId, UUID providerId) {
        CourseEntity course = courseRepository.findByIdAndProviderId(courseId, providerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

        if (course.getStatus() == CourseStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course already cancelled");
        }

        course.setStatus(CourseStatus.CANCELLED);
        courseRepository.save(course);
        slotGeneratorService.deleteFutureUnbookedSlots(course.getId());
    }

    @Transactional(readOnly = true)
    public List<CourseManagementDTO> getProviderCourses(UUID providerId) {
        return courseRepository.findByProviderIdOrderByCreatedAtDesc(providerId)
                .stream()
                .map(this::toManagementDto)
                .toList();
    }

    private CourseManagementDTO toManagementDto(CourseEntity course) {
        List<String> imageUrls = courseImageRepository
                .findByCourseIdOrderByDisplayOrderAsc(course.getId())
                .stream()
                .map(CourseImageEntity::getUrl)
                .toList();

        return new CourseManagementDTO(
                course.getId(),
                course.getName(),
                course.getDescription(),
                course.getCategory().getName(),
                course.getAddress(),
                course.getCity(),
                course.getPostcode(),
                course.getCountry(),
                course.getCapacityPerSlot(),
                course.getSlotDurationMinutes(),
                course.getScheduleConfig(),
                course.getStartDate(),
                course.getEndDate(),
                course.getStatus().name(),
                imageUrls,
                course.getCreatedAt(),
                course.getUpdatedAt()
        );
    }

    private Point createPoint(double lng, double lat) {
        return GEOMETRY_FACTORY.createPoint(new Coordinate(lng, lat));
    }
}