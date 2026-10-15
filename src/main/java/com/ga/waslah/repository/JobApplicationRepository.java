package com.ga.waslah.repository;

import com.ga.waslah.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobApplicationRepository
        extends JpaRepository<JobApplication, Long> {

    boolean existsByJobIdAndUserId(Long jobId, Long userId);

    List<JobApplication> findByUserId(Long userId);

    List<JobApplication> findByJobId(Long jobId);
}