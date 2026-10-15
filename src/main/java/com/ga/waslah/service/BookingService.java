package com.ga.waslah.service;

import com.ga.waslah.dto.BookingRequest;
import com.ga.waslah.dto.BookingResponse;
import com.ga.waslah.exception.ForbiddenException;
import com.ga.waslah.exception.ResourceNotFoundException;
import com.ga.waslah.model.Booking;
import com.ga.waslah.model.BookingStatus;
import com.ga.waslah.model.StudySpace;
import com.ga.waslah.model.User;
import com.ga.waslah.repository.BookingRepository;
import com.ga.waslah.repository.StudySpaceRepository;
import com.ga.waslah.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final StudySpaceRepository studySpaceRepository;
    private final UserRepository userRepository;

    public BookingService(
            BookingRepository bookingRepository,
            StudySpaceRepository studySpaceRepository,
            UserRepository userRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.studySpaceRepository = studySpaceRepository;
        this.userRepository = userRepository;
    }

    // =========================
    // CREATE BOOKING
    // =========================

    public BookingResponse createBooking(
            String username,
            BookingRequest request
    ) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        StudySpace studySpace = studySpaceRepository.findById(
                request.getStudySpaceId()
        ).orElseThrow(() ->
                new ResourceNotFoundException("Study space not found"));

        // The end time must be after the start time.
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new IllegalArgumentException(
                    "End time must be after start time"
            );
        }

        // The study space must currently be available for booking.
        if (!studySpace.getAvailable()) {
            throw new ForbiddenException(
                    "This study space is not available"
            );
        }

        boolean alreadyBooked =
                bookingRepository
                        .existsByStudySpaceAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
                                studySpace,
                                BookingStatus.PENDING,
                                request.getEndTime(),
                                request.getStartTime()
                        )
                        ||
                        bookingRepository
                                .existsByStudySpaceAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
                                        studySpace,
                                        BookingStatus.CONFIRMED,
                                        request.getEndTime(),
                                        request.getStartTime()
                                );

        if (alreadyBooked) {
            throw new IllegalArgumentException(
                    "This study space is already booked for the selected time"
            );
        }

        Booking booking = new Booking();

        booking.setUser(user);
        booking.setStudySpace(studySpace);
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        booking.setStatus(BookingStatus.PENDING);

        return toResponse(
                bookingRepository.save(booking)
        );
    }

    // =========================
    // GET MY BOOKINGS
    // =========================

    public List<BookingResponse> getMyBookings(String username) {

        return bookingRepository
                .findByUser_Username(username)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // =========================
    // GET BOOKING BY ID
    // =========================

    public BookingResponse getBookingById(
            Long id,
            String username
    ) {

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Booking not found"
                        ));

        checkOwnership(booking, username);

        return toResponse(booking);
    }

    // =========================
    // CANCEL BOOKING
    // =========================

    public BookingResponse cancelBooking(
            Long id,
            String username
    ) {

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Booking not found"
                        ));

        // A user can only cancel their own booking.
        checkOwnership(booking, username);

        // A completed booking cannot be cancelled.
        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new ForbiddenException(
                    "Completed bookings cannot be cancelled"
            );
        }

        // A cancelled booking cannot be cancelled again.
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new ForbiddenException(
                    "Booking is already cancelled"
            );
        }

        booking.setStatus(BookingStatus.CANCELLED);

        return toResponse(
                bookingRepository.save(booking)
        );
    }

    // =========================
    // HELPER METHODS
    // =========================

    private void checkOwnership(
            Booking booking,
            String username
    ) {

        if (!booking.getUser()
                .getUsername()
                .equals(username)) {

            throw new ForbiddenException(
                    "You are not allowed to access this booking"
            );
        }
    }

    private BookingResponse toResponse(Booking booking) {

        return new BookingResponse(
                booking.getId(),
                booking.getUser().getUsername(),
                booking.getStudySpace().getCafe().getName(),
                booking.getStudySpace().getName(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getStatus(),
                booking.getCreatedAt()
        );
    }
}