package dev.babat.spring.backend.course.service;

import dev.babat.spring.backend.course.entity.CourseEntity;
import dev.babat.spring.backend.course.entity.CourseSlotEntity;
import dev.babat.spring.backend.course.entity.CourseSlotStatus;
import dev.babat.spring.backend.course.entity.ScheduleConfig;
import dev.babat.spring.backend.course.repository.CourseSlotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SlotGeneratorService {

    private final CourseSlotRepository courseSlotRepository;

    @Transactional
    public void generateSlots(CourseEntity course) {
        LocalDate start = course.getStartDate();
        generateSlotsFrom(course, start);
    }

    @Transactional
    public void regenerateFutureSlots(CourseEntity course) {
        LocalDate today = LocalDate.now();
        courseSlotRepository.deleteUnbookedFutureSlots(course.getId(), today);
        generateSlotsFrom(course, today);
    }

    private void generateSlotsFrom(CourseEntity course, LocalDate from) {
        ScheduleConfig config = course.getScheduleConfig();
        LocalDate end = course.getEndDate();
        int durationMinutes = course.getSlotDurationMinutes();

        List<CourseSlotEntity> slots = new ArrayList<>();

        for (LocalDate date = from; !date.isAfter(end); date = date.plusDays(1)) {
            final LocalDate currentDate = date;
            int dayOfWeek = date.getDayOfWeek().getValue();

            config.rules().stream()
                    .filter(rule -> rule.dayOfWeek() == dayOfWeek)
                    .forEach(rule -> {
                        LocalTime startTime = LocalTime.parse(rule.startTime(), DateTimeFormatter.ofPattern("HH:mm"));
                        LocalTime endTime = startTime.plusMinutes(durationMinutes);

                        CourseSlotEntity slot = new CourseSlotEntity();
                        slot.setCourse(course);
                        slot.setSlotDate(currentDate);
                        slot.setStartTime(startTime);
                        slot.setEndTime(endTime);
                        slot.setStatus(CourseSlotStatus.SCHEDULED);

                        slots.add(slot);
                    });
        }

        courseSlotRepository.saveAll(slots);
        log.info("Generated {} slots for course {}", slots.size(), course.getId());
    }

    @Transactional
    public void deleteFutureUnbookedSlots(UUID courseId) {
        courseSlotRepository.deleteUnbookedFutureSlots(courseId, LocalDate.now());
    }
}