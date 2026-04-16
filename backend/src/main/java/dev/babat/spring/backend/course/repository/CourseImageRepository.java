package dev.babat.spring.backend.course.repository;

import dev.babat.spring.backend.course.entity.CourseImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CourseImageRepository extends JpaRepository<CourseImageEntity, UUID> {
    List<CourseImageEntity> findByCourseIdOrderByDisplayOrderAsc(UUID courseId);
}
