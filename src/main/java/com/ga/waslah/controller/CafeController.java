
package com.ga.waslah.controller;

import com.ga.waslah.dto.CafeRequest;
import com.ga.waslah.dto.CafeResponse;
import com.ga.waslah.dto.PageResponse;
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

    /**
     * Retrieves active cafe's with optional filtering, pagination, and sorting.
     *
     * <p>Example:
     * GET /api/cafes?page=0&size=10&name=cafe&sort=name&direction=asc
     * </p>
     *
     * @param name optional cafe name filter
     * @param location optional cafe location filter
     * @param page zero-based page number; defaults to 0
     * @param size number of results per page; defaults to 10
     * @param sort field used to sort results; defaults to name
     * @param direction sort direction; defaults to ascending
     * @return paginated active cafes and pagination metadata
     */
    @GetMapping
    public ResponseEntity<PageResponse<CafeResponse>> getAllCafes(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        // Pass search and pagination parameters to the service layer.
        return ResponseEntity.ok(
                cafeService.getActiveCafes(
                        name,
                        location,
                        page,
                        size,
                        sort,
                        direction
                )
        );
    }

    /**
     * Retrieves a cafe by its ID.
     *
     * @param id ID of the cafe
     * @return the requested cafe
     */
    @GetMapping("/{id}")
    public ResponseEntity<CafeResponse> getCafeById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                cafeService.getCafeById(id)
        );
    }

    /**
     * Retrieves cafe's managed by the authenticated user.
     *
     * @param authentication authenticated user's security information
     * @return cafe's managed by the current user
     */
    @GetMapping("/my")
    public ResponseEntity<List<CafeResponse>> getMyCafes(
            Authentication authentication
    ) {
        String username = authentication.getName();

        return ResponseEntity.ok(
                cafeService.getManagerCafes(username)
        );
    }

    /**
     * Creates a cafe for the authenticated cafe manager.
     *
     * @param request cafe details
     * @param authentication authenticated user's security information
     * @return the created cafe with HTTP status 201
     */
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

    /**
     * Updates a cafe owned by the authenticated manager.
     *
     * @param id ID of the cafe to update
     * @param request updated cafe details
     * @param authentication authenticated user's security information
     * @return the updated cafe
     */
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

    /**
     * Deactivates a cafe without deleting its database record.
     *
     * @param id ID of the cafe to deactivate
     * @param authentication authenticated user's security information
     * @return HTTP status 204 when successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateCafe(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String username = authentication.getName();

        cafeService.deactivateCafe(id, username);

        return ResponseEntity.noContent().build();
    }

    /**
     * Reactivates a cafe owned by the authenticated manager.
     *
     * @param id ID of the cafe to activate
     * @param authentication authenticated user's security information
     * @return HTTP status 204 when successful
     */
    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activateCafe(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String username = authentication.getName();

        cafeService.activateCafe(id, username);

        return ResponseEntity.noContent().build();
    }
}
