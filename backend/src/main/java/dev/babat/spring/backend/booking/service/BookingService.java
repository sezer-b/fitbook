package dev.babat.spring.backend.booking.service;

import dev.babat.spring.backend.booking.dto.BookingHistoryDTO;
import dev.babat.spring.backend.booking.dto.BookingResponse;
import dev.babat.spring.backend.booking.dto.CreateBookingRequest;
import dev.babat.spring.backend.booking.dto.SlotBookingDTO;
import dev.babat.spring.backend.booking.entity.BookingEntity;
import dev.babat.spring.backend.booking.entity.BookingStatus;
import dev.babat.spring.backend.booking.repository.BookingRepository;
import dev.babat.spring.backend.course.entity.CourseSlotEntity;
import dev.babat.spring.backend.course.entity.CourseSlotStatus;
import dev.babat.spring.backend.course.repository.CourseSlotRepository;
import dev.babat.spring.backend.user.entity.UserEntity;
import dev.babat.spring.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final CourseSlotRepository courseSlotRepository;
    private final UserRepository userRepository;

    @Transactional
    public BookingResponse book(CreateBookingRequest request, UUID userId) {
        CourseSlotEntity slot = courseSlotRepository.findById(request.slotId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Slot not found"));

        if (slot.getStatus() != CourseSlotStatus.SCHEDULED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Slot is not available");
        }

        int capacity = slot.getCourse().getCapacityPerSlot();
        int confirmed = bookingRepository.countBySlotIdAndStatus(request.slotId(), BookingStatus.CONFIRMED);

        BookingEntity booking = new BookingEntity();
        booking.setSlot(slot);

        if (userId != null) {
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
            booking.setUser(user);
        } else {
            booking.setGuestFirstName(request.guestFirstName());
            booking.setGuestLastName(request.guestLastName());
            booking.setGuestEmail(request.guestEmail());
            booking.setGuestPhone(request.guestPhone());
        }

        if (confirmed < capacity) {
            booking.setStatus(BookingStatus.CONFIRMED);
            booking.setWaitlistPosition(null);
        } else {
            int waitlistPosition = bookingRepository.countBySlotIdAndStatus(request.slotId(), BookingStatus.WAITLISTED) + 1;
            booking.setStatus(BookingStatus.WAITLISTED);
            booking.setWaitlistPosition(waitlistPosition);
        }

        BookingEntity saved = bookingRepository.save(booking);
        return toResponse(saved);
    }

    @Transactional
    public void cancel(UUID bookingId, UUID userId) {
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        if (userId != null && !booking.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your booking");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking already cancelled");
        }

        boolean wasConfirmed = booking.getStatus() == BookingStatus.CONFIRMED;
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        if (wasConfirmed) {
            promoteWaitlist(booking.getSlot().getId());
        }
    }

    @Transactional(readOnly = true)
    public List<BookingHistoryDTO> getMyBookings(UUID userId) {
        return bookingRepository.findByUserIdOrderByBookedAtDesc(userId)
                .stream()
                .map(b -> new BookingHistoryDTO(
                        b.getId(),
                        b.getSlot().getId(),
                        b.getSlot().getCourse().getName(),
                        b.getSlot().getCourse().getProvider().getBusinessName(),
                        b.getSlot().getSlotDate(),
                        b.getSlot().getStartTime(),
                        b.getSlot().getEndTime(),
                        b.getStatus().name(),
                        b.getWaitlistPosition(),
                        b.getBookedAt()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SlotBookingDTO> getBookingsForSlot(UUID slotId, UUID providerId) {
        CourseSlotEntity slot = courseSlotRepository.findById(slotId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Slot not found"));

        if (!slot.getCourse().getProvider().getId().equals(providerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your slot");
        }

        return bookingRepository.findBookingsForSlot(slotId)
                .stream()
                .map(b -> {
                    boolean isGuest = b.getGuestEmail() != null;
                    return new SlotBookingDTO(
                            b.getId(),
                            isGuest ? b.getGuestFirstName() : b.getUserFirstName(),
                            isGuest ? b.getGuestLastName() : b.getUserLastName(),
                            isGuest ? b.getGuestEmail() : b.getUserEmail(),
                            b.getStatus(),
                            b.getWaitlistPosition(),
                            isGuest,
                            b.getBookedAt()
                    );
                })
                .toList();
    }

    private void promoteWaitlist(UUID slotId) {
        List<BookingEntity> waitlisted = bookingRepository
                .findBySlotIdAndStatusOrderByWaitlistPositionAsc(slotId, BookingStatus.WAITLISTED);

        if (waitlisted.isEmpty()) return;

        BookingEntity promoted = waitlisted.getFirst();
        promoted.setStatus(BookingStatus.CONFIRMED);
        promoted.setWaitlistPosition(null);
        bookingRepository.save(promoted);

        for (int i = 1; i < waitlisted.size(); i++) {
            waitlisted.get(i).setWaitlistPosition(i);
        }
        bookingRepository.saveAll(waitlisted.subList(1, waitlisted.size()));
    }

    private BookingResponse toResponse(BookingEntity booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getSlot().getId(),
                booking.getStatus().name(),
                booking.getWaitlistPosition()
        );
    }
}