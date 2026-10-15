package com.ga.waslah.dto;

import com.ga.waslah.model.CafeRequestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CafeRegistrationDecisionRequest {

    @NotNull(message = "Decision is required")
    private CafeRequestStatus decision;
}