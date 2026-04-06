package dev.babat.spring.backend.course.entity;

import java.util.List;

public record ScheduleConfig(List<ScheduleRule> rules) {
    public record ScheduleRule(String dayOfWeek, String startTime) {
    }
}
