package com.ga.waslah.controller;

import com.ga.waslah.dto.ForgotPasswordRequest;
import com.ga.waslah.dto.LoginRequest;
import com.ga.waslah.dto.LoginResponse;
import com.ga.waslah.dto.ResetPasswordRequest;
import com.ga.waslah.exception.BadRequestException;
import com.ga.waslah.exception.ForbiddenException;
import com.ga.waslah.exception.ResourceNotFoundException;
import com.ga.waslah.model.User;
import com.ga.waslah.model.UserStatus;
import com.ga.waslah.repository.UserRepository;
import com.ga.waslah.security.JwtService;
import com.ga.waslah.service.EmailVerificationService;
import com.ga.waslah.service.PasswordResetService;
import com.ga.waslah.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.BadCredentialsException;
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final EmailVerificationService emailVerificationService;
    private final UserService userService;
    private final PasswordResetService passwordResetService;


    @PostMapping("/login")
    public LoginResponse login(
            @RequestBody LoginRequest loginRequest
    ) {

        User user = userRepository.findByUsername(
                loginRequest.getUsername()
        ).orElseThrow(() ->
                new BadRequestException(
                        "Invalid username or password"
                )
        );

        // =========================
        // ACCOUNT STATUS
        // =========================

        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new ForbiddenException(
                    "Your account is inactive and cannot be used"
            );
        }

        // =========================
        // EMAIL VERIFICATION
        // =========================

        if (!user.isEmailVerified()) {
            throw new ForbiddenException(
                    "Please verify your email before logging in"
            );
        }

        // =========================
        // TEMPORARY LOGIN LOCK
        // =========================

        if (userService.isLoginLocked(user)) {
            throw new ForbiddenException(
                    "Too many failed login attempts. Please try again later."
            );
        }

        // =========================
        // AUTHENTICATION
        // =========================

        try {

            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    loginRequest.getUsername(),
                                    loginRequest.getPassword()
                            )
                    );

            // Password is correct.
            userService.resetLoginAttempts(user);

            String token = jwtService.generateToken(
                    authentication.getName()
            );

            return new LoginResponse(token);

        } catch (BadCredentialsException exception) {

            // Password is incorrect.
            userService.recordFailedLogin(user);

            // User reached 3 failed attempts.
            if (user.getFailedLoginAttempts() >= 3) {
                throw new ForbiddenException(
                        "Too many failed login attempts. Your account is locked for 1 minute."
                );
            }

            throw new BadCredentialsException(
                    "Invalid username or password"
            );
        }
    }
    @GetMapping("/verify-email")
    public String verifyEmail(
            @RequestParam String token
    ) {

        emailVerificationService.verifyEmail(token);

        return "Email verified successfully. You can now login.";
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request
    ) {

        passwordResetService.createAndSendResetToken(
                request.email()
        );

        return ResponseEntity.ok(
                "Password reset email sent successfully"
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) {

        passwordResetService.resetPassword(
                request.token(),
                request.newPassword()
        );

        return ResponseEntity.ok(
                "Password has been reset successfully"
        );
    }

}
