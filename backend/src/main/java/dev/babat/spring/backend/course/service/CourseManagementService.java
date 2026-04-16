package dev.babat.spring.backend.course.service;

import dev.babat.spring.backend.course.entity.CourseEntity;
import dev.babat.spring.backend.course.entity.CourseStatus;
import dev.babat.spring.backend.course.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseManagementService {

    private final CourseRepository courseRepository;
    private final SlotGeneratorService slotGeneratorService;

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
}