package com.ga.waslah.service;

import com.ga.waslah.dto.StudySpaceResponse;
import com.ga.waslah.model.StudySpace;
import com.ga.waslah.repository.StudySpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudySpaceService {

    private final StudySpaceRepository studySpaceRepository;

    public List<StudySpaceResponse> getAvailableStudySpaces() {

        return studySpaceRepository
                .findByAvailableTrueAndCafe_ActiveTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private StudySpaceResponse toResponse(StudySpace studySpace) {

        return new StudySpaceResponse(
                studySpace.getId(),
                studySpace.getName(),
                studySpace.getCapacity(),
                studySpace.getPricePerHour(),
                studySpace.getCafe().getName(),
                studySpace.getCafe().getLocation()
        );
    }
}