package com.ga.waslah.controller;

import com.ga.waslah.dto.StudySpaceResponse;
import com.ga.waslah.service.StudySpaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/study-spaces")
@RequiredArgsConstructor
public class StudySpaceController {

    private final StudySpaceService studySpaceService;

    @GetMapping
    public ResponseEntity<List<StudySpaceResponse>> getAvailableStudySpaces() {

        return ResponseEntity.ok(
                studySpaceService.getAvailableStudySpaces()
        );
    }
}