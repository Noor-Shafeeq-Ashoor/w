package com.ga.waslah.controller;

import com.ga.waslah.dto.CafeRequest;
import com.ga.waslah.dto.CafeResponse;
import com.ga.waslah.service.CafeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cafes")
@RequiredArgsConstructor
public class CafeController {

    private final CafeService cafeService;

    // =========================
    // GET ALL ACTIVE CAFES
    // =========================

    @GetMapping
    public ResponseEntity<List<CafeResponse>> getAllCafes() {

        return ResponseEntity.ok(
                cafeService.getAllActiveCafes()
        );
    }

    // =========================
    // GET CAFE BY ID
    // =========================

    @GetMapping("/{id}")
    public ResponseEntity<CafeResponse> getCafeById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                cafeService.getCafeById(id)
        );
    }

    // =========================
    // GET MANAGER'S CAFES
    // =========================

    @GetMapping("/my")
    public ResponseEntity<List<CafeResponse>> getMyCafes(
            Authentication authentication
    ) {

        String username = authentication.getName();

        return ResponseEntity.ok(
                cafeService.getManagerCafes(username)
        );
    }

    // =========================
    // CREATE CAFE
    // =========================

    @PostMapping
    public ResponseEntity<CafeResponse> createCafe(
            @Valid @RequestBody CafeRequest request,
            Authentication authentication
    ) {

        String username = authentication.getName();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        cafeService.createCafe(
                                username,
                                request
                        )
                );
    }

    // =========================
    // UPDATE CAFE
    // =========================

    @PutMapping("/{id}")
    public ResponseEntity<CafeResponse> updateCafe(
            @PathVariable Long id,
            @Valid @RequestBody CafeRequest request,
            Authentication authentication
    ) {

        String username = authentication.getName();

        return ResponseEntity.ok(
                cafeService.updateCafe(
                        id,
                        username,
                        request
                )
        );
    }

    // =========================
    // DEACTIVATE CAFE
    // =========================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateCafe(
            @PathVariable Long id,
            Authentication authentication
    ) {

        String username = authentication.getName();

        cafeService.deactivateCafe(
                id,
                username
        );

        return ResponseEntity.noContent().build();
    }

    // =========================
    // ACTIVATE CAFE
    // =========================

    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activateCafe(
            @PathVariable Long id,
            Authentication authentication
    ) {

        String username = authentication.getName();

        cafeService.activateCafe(
                id,
                username
        );

        return ResponseEntity.noContent().build();
    }
}