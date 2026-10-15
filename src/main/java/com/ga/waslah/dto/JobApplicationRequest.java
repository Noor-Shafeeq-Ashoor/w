package com.ga.waslah.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobApplicationRequest {

    @Size( max = 1000, message = "Cover letter must not exceed 1000 characters")
    private String coverLetter;
}