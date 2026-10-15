package com.ga.waslah.controller;

import com.ga.waslah.dto.CafeRegistrationDecisionRequest;
import com.ga.waslah.dto.CafeRegistrationRequest;
import com.ga.waslah.dto.CafeRegistrationResponse;
import com.ga.waslah.service.CafeRegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cafe-requests")
public class CafeRegistrationController {

    private final CafeRegistrationService cafeRegistrationService;

    public CafeRegistrationController(
            CafeRegistrationService cafeRegistrationService
    ) {
        this.cafeRegistrationService = cafeRegistrationService;
    }

    // =========================
    // USER - SUBMIT REQUEST
    // =========================

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CafeRegistrationResponse submitRequest(
            Authentication authentication,
            @Valid @RequestBody CafeRegistrationRequest request
    ) {

        return cafeRegistrationService.submitRequest(
                authentication.getName(),
                request
        );
    }

    // =========================
    // USER - GET MY REQUESTS
    // =========================

    @GetMapping("/my")
    public List<CafeRegistrationResponse> getMyRequests(
            Authentication authentication
    ) {

        return cafeRegistrationService.getMyRequests(
                authentication.getName()
        );
    }

    // =========================
    // ADMIN - GET PENDING REQUESTS
    // =========================

    @GetMapping("/pending")
    public List<CafeRegistrationResponse> getPendingRequests(
            Authentication authentication
    ) {

        return cafeRegistrationService.getPendingRequests(
                authentication.getName()
        );
    }

    // =========================
    // ADMIN - APPROVE / REJECT
    // =========================

    @PutMapping("/{id}/decision")
    public CafeRegistrationResponse decideRequest(
            @PathVariable Long id,
            Authentication authentication,
            @Valid @RequestBody CafeRegistrationDecisionRequest request
    ) {

        return cafeRegistrationService.decideRequest(
                id,
                authentication.getName(),
                request
        );
    }
}