package dev.babat.spring.backend.course.controller;

import dev.babat.spring.backend.course.dto.CourseCardDTO;
import dev.babat.spring.backend.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public List<CourseCardDTO> getCoursesNearby(
            @RequestParam("lat") double lat,
            @RequestParam("lng") double lng,
            @RequestParam(defaultValue = "10", name = "radiusKm") double radiusKm
    ) {
        return courseService.findCoursesNearby(lat, lng, radiusKm);
    }
}
