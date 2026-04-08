package dev.babat.spring.backend.booking.entity;

import dev.babat.spring.backend.course.entity.CourseSlotEntity;
import dev.babat.spring.backend.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Table(name = "bookings")
@Entity
@NoArgsConstructor
@Getter
@Setter
public class BookingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_id", nullable = false)
    private CourseSlotEntity slot;
    @Column(name = "guest_first_name")
    private String guestFirstName;
    @Column(name = "guest_last_name")
    private String guestLastName;
    @Column(name = "guest_email")
    private String guestEmail;
    @Column(name = "guest_phone")
    private String guestPhone;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookingStatus status;
    @Column(name = "waitlist_position")
    private Integer waitlistPosition;
    @CreationTimestamp
    @Column(name = "booked_at", nullable = false, updatable = false)
    private OffsetDateTime bookedAt;
    @Column(name = "cancelled_at")
    private OffsetDateTime cancelledAt;
}
