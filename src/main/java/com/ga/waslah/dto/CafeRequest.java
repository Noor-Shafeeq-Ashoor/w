package com.ga.waslah.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CafeRequest {

    @NotBlank(message = "Cafe name is required")
    @Size(max = 55, message = "Cafe name must not exceed 55 characters")
    private String name;

    @NotBlank(message = "Cafe description is required")
    @Size(max = 1000, message = "Cafe description must not exceed 1000 characters")
    private String description;

    @NotBlank(message = "Cafe location is required")
    private String location;
}