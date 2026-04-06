package dev.babat.spring.backend.course.entity.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.babat.spring.backend.course.entity.ScheduleConfig;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ScheduleConfigConverter implements AttributeConverter<ScheduleConfig, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(ScheduleConfig attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error converting ScheduleConfig to JSON", e);
        }
    }

    @Override
    public ScheduleConfig convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, ScheduleConfig.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error converting JSON to ScheduleConfig", e);
        }
    }
}