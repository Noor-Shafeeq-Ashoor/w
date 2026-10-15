package com.ga.waslah.controller;

import com.ga.waslah.dto.EmployerRequestRequest;
import com.ga.waslah.dto.EmployerRequestResponse;
import com.ga.waslah.model.EmployerRequest;
import com.ga.waslah.service.EmployerRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employer-requests")
@RequiredArgsConstructor
public class EmployerRequestController {

    private final EmployerRequestService employerRequestService;

    private EmployerRequestResponse toResponse(
            EmployerRequest employerRequest
    ) {

        return new EmployerRequestResponse(
                employerRequest.getCompanyName(),
                employerRequest.getCompanyDescription(),
                employerRequest.getJobTypes(),
                employerRequest.getCommercialRegistration(),
                employerRequest.getStatus()
        );
    }

    @PostMapping
    public ResponseEntity<EmployerRequestResponse> createRequest(
            @Valid @RequestBody EmployerRequestRequest request,
            Authentication authentication
    ) {

        EmployerRequest employerRequest =
                employerRequestService.createRequest(
                        authentication.getName(),
                        request
                );

        return new ResponseEntity<>(
                toResponse(employerRequest),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/my")
    public ResponseEntity<EmployerRequestResponse> getMyRequest(
            Authentication authentication
    ) {

        EmployerRequest employerRequest =
                employerRequestService.getMyRequest(
                        authentication.getName()
                );

        return ResponseEntity.ok(
                toResponse(employerRequest)
        );
    }
}
