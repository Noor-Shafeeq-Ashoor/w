package com.ga.waslah.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployerRequestRequest {

    @NotBlank(message = "Company name is required")
    @Size(max = 150, message = "Company name must not exceed 150 characters")
    private String companyName;

    @NotBlank(message = "Company description is required")
    @Size(max = 1000, message = "Company description must not exceed 1000 characters")
    private String companyDescription;

    @NotBlank(message = "Job types are required")
    private String jobTypes;

    @NotBlank(message = "Commercial registration is required")
    private String commercialRegistration;
}