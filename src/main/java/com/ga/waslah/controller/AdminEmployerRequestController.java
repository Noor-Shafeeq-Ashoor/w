package com.ga.waslah.controller;

import com.ga.waslah.dto.EmployerRequestResponse;

import com.ga.waslah.model.EmployerRequest;
import com.ga.waslah.service.EmployerRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/employer-requests")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminEmployerRequestController {

    private final EmployerRequestService employerRequestService;

    @GetMapping
    public ResponseEntity<List<EmployerRequestResponse>> getPendingRequests() {

        List<EmployerRequestResponse> response =
                employerRequestService.getPendingRequests()
                        .stream()
                        .map(this::toDTO)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<EmployerRequestResponse> approveRequest(
            @PathVariable Long id
    ) {

        EmployerRequest request =
                employerRequestService.approveRequest(id);

        return ResponseEntity.ok(toDTO(request));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<EmployerRequestResponse> rejectRequest(
            @PathVariable Long id
    ) {

        EmployerRequest request =
                employerRequestService.rejectRequest(id);

        return ResponseEntity.ok(toDTO(request));
    }

    private EmployerRequestResponse toDTO(EmployerRequest request) {

        return new EmployerRequestResponse(
                request.getCompanyName(),
                request.getCompanyDescription(),
                request.getJobTypes(),
                request.getCommercialRegistration(),
                request.getStatus()
        );
    }
}