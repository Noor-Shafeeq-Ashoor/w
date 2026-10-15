package com.ga.waslah.service;

import com.ga.waslah.dto.BookingRequest;
import com.ga.waslah.dto.BookingResponse;
import com.ga.waslah.exception.*;
import com.ga.waslah.model.*;
import com.ga.waslah.repository.BookingRepository;
import com.ga.waslah.repository.StudySpaceRepository;
import com.ga.waslah.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private StudySpaceRepository studySpaceRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingService bookingService;


    @Test
    void shouldRejectBookingWhenStudySpaceIsAlreadyBooked() {

        User user = new User();
        user.setUsername("noor");

        StudySpace studySpace = new StudySpace();
        studySpace.setAvailable(true);

        LocalDateTime startTime =
                LocalDateTime.of(2026, 10, 8, 14, 0);

        LocalDateTime endTime =
                LocalDateTime.of(2026, 10, 8, 16, 0);

        BookingRequest request = new BookingRequest();

        request.setStudySpaceId(1L);
        request.setStartTime(startTime);
        request.setEndTime(endTime);

        when(userRepository.findByUsername("noor"))
                .thenReturn(Optional.of(user));

        when(studySpaceRepository.findById(1L))
                .thenReturn(Optional.of(studySpace));

        // Existing booking overlaps with requested time
        when(bookingRepository
                .existsByStudySpaceAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
                        studySpace,
                        BookingStatus.PENDING,
                        endTime,
                        startTime
                ))
                .thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.createBooking("noor", request)
        );

        verify(bookingRepository, never()).save(any());
    }
    @Test
    void shouldCancelOwnBooking() {

        User user = new User();
        user.setUsername("noor");

        StudySpace studySpace = new StudySpace();
        studySpace.setAvailable(true);

        Booking booking = new Booking();

        booking.setId(1L);
        booking.setUser(user);
        booking.setStudySpace(studySpace);
        booking.setStatus(BookingStatus.PENDING);

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        when(bookingRepository.save(booking))
                .thenReturn(booking);

        BookingResponse response =
                bookingService.cancelBooking(1L, "noor");

        assertEquals(
                BookingStatus.CANCELLED,
                booking.getStatus()
        );

        verify(bookingRepository).save(booking);
    }
}