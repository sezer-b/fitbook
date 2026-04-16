package dev.babat.spring.backend.course.repository;

import dev.babat.spring.backend.course.dto.SlotProjection;
import dev.babat.spring.backend.course.entity.CourseSlotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface CourseSlotRepository extends JpaRepository<CourseSlotEntity, UUID> {
    @Query(value = """
            SELECT cs.id, cs.slot_date, cs.start_time, cs.end_time, cs.status,
                   COUNT(b.id) AS confirmed_bookings
            FROM course_slots cs
            LEFT JOIN bookings b ON b.slot_id = cs.id AND b.status = 'CONFIRMED'
            WHERE cs.course_id = :courseId
            AND cs.slot_date >= :from
            AND cs.slot_date < :to
            AND cs.status = 'SCHEDULED'
            GROUP BY cs.id, cs.slot_date, cs.start_time, cs.end_time, cs.status
            ORDER BY cs.slot_date ASC, cs.start_time ASC
            """, nativeQuery = true)
    List<SlotProjection> findUpcomingSlots(
            @Param("courseId") UUID courseId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    @Modifying
    @Query(value = """
        DELETE FROM course_slots cs
        WHERE cs.course_id = :courseId
        AND cs.slot_date >= :from
        AND NOT EXISTS (
            SELECT 1 FROM bookings b
            WHERE b.slot_id = cs.id
            AND b.status = 'CONFIRMED'
        )
        """, nativeQuery = true)
    void deleteUnbookedFutureSlots(@Param("courseId") UUID courseId, @Param("from") LocalDate from);
}
