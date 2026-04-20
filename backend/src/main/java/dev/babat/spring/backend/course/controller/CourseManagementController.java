package dev.babat.spring.backend.course.controller;

import dev.babat.spring.backend.booking.dto.SlotBookingDTO;
import dev.babat.spring.backend.booking.service.BookingService;
import dev.babat.spring.backend.course.dto.CourseManagementDTO;
import dev.babat.spring.backend.course.dto.CreateCourseRequest;
import dev.babat.spring.backend.course.dto.UpdateCourseRequest;
import dev.babat.spring.backend.course.service.CourseImageService;
import dev.babat.spring.backend.course.service.CourseManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/provider/courses")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PROVIDER')")
public class CourseManagementController {

    private final CourseManagementService courseManagementService;
    private final CourseImageService courseImageService;
    private final BookingService bookingService;

    @GetMapping
    public List<CourseManagementDTO> getMyCourses(Authentication authentication) {
        return courseManagementService.getProviderCourses(providerId(authentication));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourseManagementDTO createCourse(@Valid @RequestBody CreateCourseRequest request,
                                            Authentication authentication) {
        return courseManagementService.createCourse(request, providerId(authentication));
    }

    @PatchMapping("/{id}")
    public CourseManagementDTO updateCourse(@PathVariable(name = "id") UUID id,
                                            @RequestBody UpdateCourseRequest request,
                                            Authentication authentication) {
        return courseManagementService.updateCourse(id, request, providerId(authentication));
    }

    @PostMapping("/{id}/publish")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void publishCourse(@PathVariable(name = "id") UUID id, Authentication authentication) {
        courseManagementService.publishCourse(id, providerId(authentication));
    }

    @PostMapping("/{id}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelCourse(@PathVariable(name = "id") UUID id, Authentication authentication) {
        courseManagementService.cancelCourse(id, providerId(authentication));
    }

    @PostMapping("/{id}/images")
    @ResponseStatus(HttpStatus.CREATED)
    public String uploadImage(@PathVariable(name = "id") UUID id,
                              @RequestParam(name = "file") MultipartFile file,
                              Authentication authentication) throws IOException {
        return courseImageService.uploadImage(id, providerId(authentication), file);
    }

    @DeleteMapping("/{id}/images/{imageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteImage(@PathVariable(name = "id") UUID id,
                            @PathVariable(name = "imageId") UUID imageId,
                            Authentication authentication) {
        courseImageService.deleteImage(id, imageId, providerId(authentication));
    }

    @GetMapping("/slots/{slotId}/bookings")
    @PreAuthorize("hasRole('PROVIDER')")
    public List<SlotBookingDTO> getSlotBookings(@PathVariable(name = "slotId") UUID slotId,
                                                Authentication authentication) {
        return bookingService.getBookingsForSlot(slotId, providerId(authentication));
    }

    private UUID providerId(Authentication authentication) {
        return UUID.fromString(authentication.getName());
    }
}