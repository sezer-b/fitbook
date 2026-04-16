package dev.babat.spring.backend.course.service;

import dev.babat.spring.backend.course.dto.*;
import dev.babat.spring.backend.course.entity.CourseImageEntity;
import dev.babat.spring.backend.course.entity.CourseSlotStatus;
import dev.babat.spring.backend.course.repository.CourseImageRepository;
import dev.babat.spring.backend.course.repository.CourseRepository;
import dev.babat.spring.backend.course.repository.CourseSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseImageRepository courseImageRepository;
    private final CourseSlotRepository courseSlotRepository;

    @Transactional(readOnly = true)
    public List<CourseCardDTO> findCoursesNearby(double lat, double lng, double radiusKm) {
        double radiusMeters = radiusKm * 1000;
        List<CourseCardProjection> results = courseRepository.findCoursesNearby(lat, lng, radiusMeters);
        return results.stream()
                .map(p -> new CourseCardDTO(
                        p.getId(),
                        p.getName(),
                        p.getCategory(),
                        p.getProviderName(),
                        p.getCity(),
                        p.getCountry(),
                        p.getDistanceKm(),
                        p.getImageUrl(),
                        p.getCapacityPerSlot(),
                        p.getSlotDurationMinutes()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public CourseDetailDTO getCourseDetail(UUID id, double lat, double lng, LocalDate from) {
        CourseDetailProjection course = courseRepository.findCourseDetail(id, lat, lng)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

        List<String> imageUrls = courseImageRepository.findByCourseIdOrderByDisplayOrderAsc(id)
                .stream()
                .map(CourseImageEntity::getUrl)
                .toList();

        List<SlotProjection> slots = courseSlotRepository.findUpcomingSlots(id, from, from.plusDays(30));

        Map<LocalDate, List<SlotDTO>> slotsByDate = slots.stream()
                .collect(Collectors.groupingBy(
                        SlotProjection::getSlotDate,
                        TreeMap::new,
                        Collectors.mapping(
                                s -> new SlotDTO(
                                        s.getId(),
                                        s.getStartTime(),
                                        s.getEndTime(),
                                        course.getCapacityPerSlot() - s.getConfirmedBookings(),
                                        CourseSlotStatus.valueOf(s.getStatus())
                                ),
                                Collectors.toList()
                        )
                ));

        return new CourseDetailDTO(
                course.getId(),
                course.getName(),
                course.getDescription(),
                course.getCategory(),
                course.getProviderName(),
                course.getAddress(),
                course.getCity(),
                course.getPostcode(),
                course.getCountry(),
                course.getDistanceKm(),
                course.getCapacityPerSlot(),
                course.getSlotDurationMinutes(),
                imageUrls,
                slotsByDate
        );
    }
}