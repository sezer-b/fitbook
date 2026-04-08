package dev.babat.spring.backend.course.repository;

import dev.babat.spring.backend.course.dto.CourseCardProjection;
import dev.babat.spring.backend.course.entity.CourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<CourseEntity, UUID> {
    @Query(value = """
        SELECT c.id, c.name, c.city, c.country,
               c.capacity_per_slot, c.slot_duration_minutes,
               cat.name AS category,
               p.business_name AS provider_name,
               ST_Distance(c.location, ST_MakePoint(:lng, :lat)::geography) / 1000 AS distance_km,
               (SELECT ci.url FROM course_images ci
                WHERE ci.course_id = c.id
                ORDER BY ci.display_order ASC
                LIMIT 1) AS image_url
        FROM courses c
        JOIN categories cat ON cat.id = c.category_id
        JOIN providers p ON p.id = c.provider_id
        WHERE c.status = 'ACTIVE'
        AND ST_DWithin(c.location, ST_MakePoint(:lng, :lat)::geography, :radiusMeters)
        ORDER BY distance_km ASC
        """, nativeQuery = true)
    List<CourseCardProjection> findCoursesNearby(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radiusMeters") double radiusMeters
    );
}
