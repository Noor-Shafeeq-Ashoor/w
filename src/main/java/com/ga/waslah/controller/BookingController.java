
package com.ga.waslah.controller;

import com.ga.waslah.dto.BookingRequest;
import com.ga.waslah.dto.BookingResponse;
import com.ga.waslah.service.BookingService;
import com.ga.waslah.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Handles HTTP requests related to bookings.*/
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final NotificationService notificationService;

    /**
     * Creates a booking for the authenticated user.
     * If booking creation fails, sends a real-time failure
     * notification to the user who attempted the booking.
     * The exception is rethrown so the application's normal
     * error handling can process the failed request.
     * @param request booking details
     * @param authentication authenticated user's security information
     * @return the created booking with HTTP status 201
     */
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingRequest request,
            Authentication authentication
    ) {
        String username = authentication.getName();

        try {
            BookingResponse response =
                    bookingService.createBooking(username, request);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);

        } catch (RuntimeException exception) {
            notificationService.sendToUser(
                    username,
                    "BOOKING_FAILED",
                    "Your booking could not be completed. "
                            + "Please review the details and try again."
            );

            throw exception;
        }
    }
}
