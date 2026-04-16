package dev.babat.spring.backend.course.controller;

import dev.babat.spring.backend.course.dto.CourseCardDTO;
import dev.babat.spring.backend.course.dto.CourseDetailDTO;
import dev.babat.spring.backend.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public List<CourseCardDTO> getCoursesNearby(
            @RequestParam(name = "lat") double lat,
            @RequestParam(name = "lng") double lng,
            @RequestParam(defaultValue = "10", name = "radiusKm") double radiusKm
    ) {
        return courseService.findCoursesNearby(lat, lng, radiusKm);
    }

    @GetMapping("/{id}")
    public CourseDetailDTO getCourseDetail(
            @PathVariable(name = "id") UUID id,
            @RequestParam(name = "lat") double lat,
            @RequestParam(name = "lng") double lng,
            @RequestParam(required = false, name = "from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from
    ) {
        if (from == null) {
            from = LocalDate.now();
        }
        return courseService.getCourseDetail(id, lat, lng, from);
    }
}
