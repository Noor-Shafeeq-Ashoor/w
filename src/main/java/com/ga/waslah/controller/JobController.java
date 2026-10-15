package com.ga.waslah.controller;

import com.ga.waslah.dto.JobRequest;
import com.ga.waslah.dto.JobResponse;
import com.ga.waslah.service.JobService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    // =========================
    // CREATE
    // =========================

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<JobResponse> createJob(
            @Valid @RequestBody JobRequest request,
            Authentication authentication
    ) {

        JobResponse response = jobService.createJob(
                authentication.getName(),
                request
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =========================
    // GET ALL
    // =========================

    @GetMapping
    public ResponseEntity<List<JobResponse>> getAllJobs() {

        return ResponseEntity.ok(
                jobService.getAllJobs()
        );
    }

    // =========================
    // GET BY ID
    // =========================

    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getJobById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                jobService.getJobById(id)
        );
    }

    // =========================
    // UPDATE
    // =========================

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<JobResponse> updateJob(
            @PathVariable Long id,
            @Valid @RequestBody JobRequest request,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                jobService.updateJob(
                        id,
                        authentication.getName(),
                        request
                )
        );
    }

    // =========================
    // CLOSE
    // =========================

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<Void> deleteJob(
            @PathVariable Long id,
            Authentication authentication
    ) {

        jobService.deleteJob(
                id,
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }
}