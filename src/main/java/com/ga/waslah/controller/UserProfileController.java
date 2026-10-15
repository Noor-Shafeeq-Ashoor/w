package com.ga.waslah.controller;

import com.ga.waslah.dto.UserProfileRequest;
import com.ga.waslah.model.UserProfile;
import com.ga.waslah.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/profile")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping
    public ResponseEntity<UserProfile> getProfile(
            Authentication authentication
    ) {

        String username = authentication.getName();

        return ResponseEntity.ok(
                userProfileService.getProfile(username)
        );
    }

    @PutMapping
    public ResponseEntity<UserProfile> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UserProfileRequest request
    ) {

        String username = authentication.getName();

        return ResponseEntity.ok(
                userProfileService.updateProfile(username, request)
        );
    }

    @PutMapping("/image")
    public ResponseEntity<UserProfile> updateProfileImage(
            @RequestParam("image") MultipartFile image,
            Authentication authentication
    ) {

        UserProfile profile = userProfileService.updateProfileImage(
                authentication.getName(),
                image
        );

        return ResponseEntity.ok(profile);
    }
}