package com.ga.waslah.controller;

import com.ga.waslah.dto.BookingResponse;
import com.ga.waslah.service.CafeManagerBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager/bookings")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CAFE_MANAGER')")
public class CafeManagerBookingController {

    private final CafeManagerBookingService bookingService;

    @GetMapping
    public ResponseEntity<List<BookingResponse>> getMyCafeBookings(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                bookingService.getMyCafeBookings(
                        authentication.getName()
                )
        );
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<BookingResponse> confirmBooking(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                bookingService.confirmBooking(
                        id,
                        authentication.getName()
                )
        );
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> rejectBooking(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                bookingService.rejectBooking(
                        id,
                        authentication.getName()
                )
        );
    }
}