package dev.babat.spring.backend.course.service;

import dev.babat.spring.backend.course.dto.CourseCardDTO;
import dev.babat.spring.backend.course.dto.CourseCardProjection;
import dev.babat.spring.backend.course.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

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
}