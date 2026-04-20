package dev.babat.spring.backend.booking.controller;

import dev.babat.spring.backend.booking.dto.BookingHistoryDTO;
import dev.babat.spring.backend.booking.dto.BookingResponse;
import dev.babat.spring.backend.booking.dto.CreateBookingRequest;
import dev.babat.spring.backend.booking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse book(@Valid @RequestBody CreateBookingRequest request,
                                Authentication authentication) {
        UUID userId = authentication != null
                ? UUID.fromString(authentication.getName())
                : null;
        return bookingService.book(request, userId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable(name = "id") UUID id, Authentication authentication) {
        UUID userId = authentication != null
                ? UUID.fromString(authentication.getName())
                : null;
        bookingService.cancel(id, userId);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public List<BookingHistoryDTO> getMyBookings(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        return bookingService.getMyBookings(userId);
    }
}