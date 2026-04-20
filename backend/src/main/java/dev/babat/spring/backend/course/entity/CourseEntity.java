package dev.babat.spring.backend.course.entity;

import dev.babat.spring.backend.course.entity.converter.ScheduleConfigConverter;
import dev.babat.spring.backend.provider.entity.ProviderEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Table(name = "courses")
@Entity
@NoArgsConstructor
@Getter
@Setter
public class CourseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private ProviderEntity provider;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "address", nullable = false)
    private String address;
    @Column(name = "city", nullable = false)
    private String city;
    @Column(name = "postcode")
    private String postcode;
    @Column(name = "country", nullable = false)
    private String country;
    @Column(name = "capacity_per_slot", nullable = false)
    private int capacityPerSlot;
    @Column(name = "slot_duration_minutes", nullable = false)
    private int slotDurationMinutes;
    @Convert(converter = ScheduleConfigConverter.class)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "schedule_config", nullable = false, columnDefinition = "jsonb")
    private ScheduleConfig scheduleConfig;
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CourseStatus status;
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
    @Column(name = "location", nullable = false, columnDefinition = "geography(POINT, 4326)")
    private Point location;

    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseImageEntity> images = new ArrayList<>();
}
