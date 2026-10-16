package com.ga.waslah.controller;

import com.ga.waslah.dto.ChangePasswordRequest;
import com.ga.waslah.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserManagementController {

    private final UserService userService;

    /**
     * Deactivates a user account on behalf of the authenticated admin.
     *
     * @param id ID of the account to deactivate
     * @param authentication authenticated administrator's identity
     * @return HTTP 204 if the account is deactivated successfully
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateUser(
            @PathVariable Long id,
            Authentication authentication
    ) {

        userService.deactivateUser(id, authentication.getName());

        return ResponseEntity.noContent().build();
    }

    /**
     * Changes the password for the currently authenticated user.
     *
     * @param request current and new password details
     * @param authentication identity of the authenticated user
     * @return HTTP 204 if the password is changed successfully
     */
    @PutMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication
    ) {

        userService.changePassword(
                authentication.getName(),
                request
        );

        return ResponseEntity.noContent().build();
    }
}
