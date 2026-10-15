package com.ga.waslah.repository;

import com.ga.waslah.model.Booking;
import com.ga.waslah.model.BookingStatus;
import com.ga.waslah.model.StudySpace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUser_Username(String username);

    List<Booking> findByStudySpace(StudySpace studySpace);


    boolean existsByStudySpaceAndStatusInAndStartTimeLessThanAndEndTimeGreaterThan(
            StudySpace studySpace,
            List<BookingStatus> statuses,
            LocalDateTime requestedEndTime,
            LocalDateTime requestedStartTime
    );

    List<Booking> findByStudySpace_Cafe_Manager_Username(String username);
}