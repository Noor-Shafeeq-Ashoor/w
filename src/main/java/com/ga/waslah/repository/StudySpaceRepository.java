package com.ga.waslah.repository;

import com.ga.waslah.model.StudySpace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudySpaceRepository extends JpaRepository<StudySpace, Long> {

    List<StudySpace> findByAvailableTrueAndCafe_ActiveTrue();
}