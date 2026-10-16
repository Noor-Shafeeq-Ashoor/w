package com.ga.waslah.controller;

import com.ga.waslah.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    /**
     * Deactivates a user account on behalf of the authenticated admin.
     *
     * @param id ID of the account to deactivate
     * @param authentication authenticated administrator's identity
     * @return HTTP 204 if the account is deactivated successfully
     */
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateUser(
            @PathVariable Long id,
            Authentication authentication
    ) {

        userService.deactivateUser(id, authentication.getName());

        return ResponseEntity.noContent().build();
    }
}
