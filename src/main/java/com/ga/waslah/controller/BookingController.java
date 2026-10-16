
package com.ga.waslah.controller;

import com.ga.waslah.dto.BookingRequest;
import com.ga.waslah.dto.BookingResponse;
import com.ga.waslah.dto.PageResponse;
import com.ga.waslah.model.BookingStatus;
import com.ga.waslah.service.BookingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * REST controller for user booking operations.
 * <p>Users can create, view, and cancel their own bookings.</p>
 */
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    /**
     * Creates a booking for the authenticated user.
     *
     * @param request booking details
     * @param authentication authenticated user's security information
     * @return created booking with HTTP status 201
     */
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        bookingService.createBooking(
                                authentication.getName(),
                                request
                        )
                );
    }

    /** Retrieves the authenticated user's bookings with optional filters.*/
    @GetMapping
    public ResponseEntity<PageResponse<BookingResponse>> getMyBookings(
            Authentication authentication,
            @RequestParam(required = false) BookingStatus status,
            @RequestParam(required = false) Long studySpaceId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        return ResponseEntity.ok(
                bookingService.getMyBookings(
                        authentication.getName(),
                        status,
                        studySpaceId,
                        from,
                        to,
                        page,
                        size,
                        sort,
                        direction
                )
        );
    }

    /**
     * Retrieves one booking after verifying ownership.
     *
     * @param id booking ID
     * @param authentication authenticated user's security information
     * @return the requested booking
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                bookingService.getBookingById(
                        id,
                        authentication.getName()
                )
        );
    }

  // cancel booking
    @PutMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                bookingService.cancelBooking(
                        id,
                        authentication.getName()
                )
        );
    }
}
