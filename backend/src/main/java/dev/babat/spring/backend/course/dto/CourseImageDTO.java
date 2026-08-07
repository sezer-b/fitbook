package dev.babat.spring.backend.course.dto;

import java.util.UUID;

public record CourseImageDTO(UUID id, String url, int displayOrder) {
}
