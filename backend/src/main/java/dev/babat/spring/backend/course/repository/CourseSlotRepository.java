package dev.babat.spring.backend.course.repository;

import dev.babat.spring.backend.course.entity.CourseSlotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CourseSlotRepository extends JpaRepository<CourseSlotEntity, UUID> {
}
