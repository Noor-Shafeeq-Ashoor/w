package com.ga.waslah.service;

import com.ga.waslah.dto.JobRequest;
import com.ga.waslah.dto.JobResponse;
import com.ga.waslah.exception.ForbiddenException;
import com.ga.waslah.exception.ResourceNotFoundException;
import com.ga.waslah.model.Job;
import com.ga.waslah.model.JobStatus;
import com.ga.waslah.model.Role;
import com.ga.waslah.model.User;
import com.ga.waslah.repository.JobRepository;
import com.ga.waslah.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public JobService(
            JobRepository jobRepository,
            UserRepository userRepository
    ) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    // =========================
    // CREATE JOB
    // =========================

    public JobResponse createJob(
            String username,
            JobRequest request
    ) {

        User user = getUser(username);

        // Only approved employers can create jobs.
        // Approved employer = role EMPLOYER.
        if (user.getRole() != Role.EMPLOYER) {
            throw new ForbiddenException(
                    "Only approved employers can create jobs"
            );
        }

        Job job = new Job();

        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setLocation(request.getLocation());
        job.setJobType(request.getJobType());
        job.setSalary(request.getSalary());
        job.setDeadline(request.getDeadline());

        /*
         * The company name should come from the authenticated
         * employer rather than trusting the request body.
         *
         * For now we keep the field in JobRequest because it is
         * already part of the project structure.
         */
        job.setCompanyName(request.getCompanyName());

        job.setStatus(JobStatus.OPEN);
        job.setCreatedBy(user);

        return toResponse(jobRepository.save(job));
    }

    // =========================
    // GET ALL JOBS
    // =========================

    public List<JobResponse> getAllJobs() {

        return jobRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // =========================
    // GET JOB BY ID
    // =========================

    public JobResponse getJobById(Long id) {

        Job job = jobRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Job not found"
                        ));

        return toResponse(job);
    }

    // =========================
    // UPDATE JOB
    // =========================

    public JobResponse updateJob(
            Long id,
            String username,
            JobRequest request
    ) {

        User user = getUser(username);

        // Extra service-level security
        if (user.getRole() != Role.EMPLOYER) {
            throw new ForbiddenException(
                    "Only approved employers can update jobs"
            );
        }

        Job job = jobRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Job not found"
                        ));

        // Ownership check
        checkOwnership(job, username);

        // Do not allow updating a closed job
        if (job.getStatus() == JobStatus.CLOSED) {
            throw new ForbiddenException(
                    "Closed jobs cannot be updated"
            );
        }

        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setCompanyName(request.getCompanyName());
        job.setLocation(request.getLocation());
        job.setJobType(request.getJobType());
        job.setSalary(request.getSalary());
        job.setDeadline(request.getDeadline());

        return toResponse(jobRepository.save(job));
    }

    // =========================
    // DELETE / CLOSE JOB
    // =========================

    public void deleteJob(
            Long id,
            String username
    ) {

        User user = getUser(username);

        if (user.getRole() != Role.EMPLOYER) {
            throw new ForbiddenException(
                    "Only approved employers can close jobs"
            );
        }

        Job job = jobRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Job not found"
                        ));

        // Ownership check
        checkOwnership(job, username);

        // Already closed
        if (job.getStatus() == JobStatus.CLOSED) {
            throw new ForbiddenException(
                    "Job is already closed"
            );
        }

        /*
         * We do not physically delete the job.
         * We close it instead.
         */
        job.setStatus(JobStatus.CLOSED);

        jobRepository.save(job);
    }

    // =========================
    // HELPER METHODS
    // =========================

    private User getUser(String username) {

        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        ));
    }

    private void checkOwnership(
            Job job,
            String username
    ) {

        if (job.getCreatedBy() == null ||
                !job.getCreatedBy()
                        .getUsername()
                        .equals(username)) {

            throw new ForbiddenException(
                    "You are not allowed to modify this job"
            );
        }
    }

    private JobResponse toResponse(Job job) {

        return new JobResponse(
                job.getId(),
                job.getTitle(),
                job.getDescription(),
                job.getCompanyName(),
                job.getLocation(),
                job.getJobType(),
                job.getSalary(),
                job.getDeadline(),
                job.getStatus(),
                job.getCreatedAt(),
                job.getUpdatedAt(),
                job.getCreatedBy().getUsername()
        );
    }
}