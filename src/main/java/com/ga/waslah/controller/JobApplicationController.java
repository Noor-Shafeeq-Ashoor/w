package com.ga.waslah.controller;

import com.ga.waslah.dto.JobApplicationRequest;
import com.ga.waslah.dto.JobApplicationResponse;
import com.ga.waslah.model.ApplicationStatus;
import com.ga.waslah.service.JobApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    // =========================
    // APPLY FOR JOB
    // =========================

    @PostMapping("/jobs/{jobId}")
    public ResponseEntity<JobApplicationResponse> applyForJob(
            @PathVariable Long jobId,
            @Valid @RequestBody JobApplicationRequest request,
            Authentication authentication
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        jobApplicationService.applyForJob(
                                jobId,
                                authentication.getName(),
                                request
                        )
                );
    }

    // =========================
    // MY APPLICATIONS
    // =========================

    @GetMapping("/my")
    public ResponseEntity<List<JobApplicationResponse>>
    getMyApplications(
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                jobApplicationService.getMyApplications(
                        authentication.getName()
                )
        );
    }

    // =========================
    // JOB APPLICATIONS
    // =========================

    @GetMapping("/jobs/{jobId}")
    public ResponseEntity<List<JobApplicationResponse>>
    getJobApplications(
            @PathVariable Long jobId,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                jobApplicationService.getJobApplications(
                        jobId,
                        authentication.getName()
                )
        );
    }

    // =========================
    // UPDATE STATUS
    // =========================

    @PutMapping("/{applicationId}/status")
    public ResponseEntity<JobApplicationResponse>
    updateApplicationStatus(
            @PathVariable Long applicationId,
            @RequestParam ApplicationStatus status,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                jobApplicationService.updateApplicationStatus(
                        applicationId,
                        status,
                        authentication.getName()
                )
        );
    }
}