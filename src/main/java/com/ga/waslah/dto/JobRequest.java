package com.ga.waslah.dto;

import com.ga.waslah.model.JobType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class JobRequest {

    @NotBlank(message = "Title is required")
    @Size(
            max = 150,
            message = "Title must not exceed 150 characters"
    )
    private String title;

    @NotBlank(message = "Description is required")
    @Size(
            max = 2000,
            message = "Description must not exceed 2000 characters"
    )
    private String description;

    @NotBlank(message = "Company name is required")
    @Size(
            max = 150,
            message = "Company name must not exceed 150 characters"
    )
    private String companyName;

    @NotBlank(message = "Location is required")
    @Size(
            max = 150,
            message = "Location must not exceed 150 characters"
    )
    private String location;

    @NotNull(message = "Job type is required")
    private JobType jobType;

    @PositiveOrZero(message = "Salary cannot be negative")
    private Double salary;

    @NotNull(message = "Deadline is required")
    @Future(message = "Deadline must be in the future")
    private LocalDateTime deadline;
}