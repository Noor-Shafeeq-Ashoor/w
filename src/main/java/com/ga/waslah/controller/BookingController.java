package com.ga.waslah.controller;

import com.ga.waslah.dto.BookingRequest;
import com.ga.waslah.dto.BookingResponse;
import com.ga.waslah.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // =========================
    // CREATE BOOKING
    // =========================

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingRequest request,
            Authentication authentication
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        bookingService.createBooking(
                                authentication.getName(),
                                request
                        )
                );
    }

    // =========================
    // GET MY BOOKINGS
    // =========================

    @GetMapping
    public ResponseEntity<List<BookingResponse>> getMyBookings(
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                bookingService.getMyBookings(
                        authentication.getName()
                )
        );
    }

    // =========================
    // GET MY BOOKING BY ID
    // =========================

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

    // =========================
    // CANCEL MY BOOKING
    // =========================

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