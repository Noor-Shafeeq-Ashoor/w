package com.ga.waslah.repository;

import com.ga.waslah.model.Job;
import com.ga.waslah.model.JobStatus;
import com.ga.waslah.model.JobType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByStatus(JobStatus status);

    List<Job> findByJobType(JobType jobType);

    List<Job> findByLocationIgnoreCase(String location);

    List<Job> findByTitleContainingIgnoreCase(String title);
}