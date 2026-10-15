package com.ga.waslah.service;

import com.ga.waslah.exception.BadRequestException;
import com.ga.waslah.exception.ResourceNotFoundException;
import com.ga.waslah.model.PasswordResetToken;
import com.ga.waslah.model.User;
import com.ga.waslah.repository.PasswordResetTokenRepository;
import com.ga.waslah.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final MockEmailService mockEmailService;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(
            PasswordResetTokenRepository tokenRepository,
            UserRepository userRepository,
            MockEmailService mockEmailService,
            PasswordEncoder passwordEncoder
    ) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.mockEmailService = mockEmailService;
        this.passwordEncoder = passwordEncoder;
    }

    public void createAndSendResetToken(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User with this email does not exist"
                        )
                );

        // Remove any previous reset token.
        tokenRepository.deleteByUser(user);

        // Generate a new reset token.
        String tokenValue = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();

        resetToken.setToken(tokenValue);
        resetToken.setUser(user);
        resetToken.setExpiryDate(
                LocalDateTime.now().plusMinutes(15)
        );
        resetToken.setUsed(false);

        tokenRepository.save(resetToken);

        String resetLink =
                "http://localhost:8080/auth/reset-password?token="
                        + tokenValue;

        mockEmailService.sendPasswordResetEmail(
                user.getEmail(),
                resetLink
        );
    }

    @Transactional
    public void resetPassword(
            String tokenValue,
            String newPassword
    ) {

        PasswordResetToken resetToken =
                tokenRepository.findByToken(tokenValue)
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Invalid password reset token"
                                )
                        );

        if (resetToken.getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            tokenRepository.delete(resetToken);

            throw new BadRequestException(
                    "Password reset token has expired"
            );
        }

        if (resetToken.isUsed()) {
            throw new BadRequestException(
                    "Password reset token has already been used"
            );
        }

        User user = resetToken.getUser();

        user.setPassword(
                passwordEncoder.encode(newPassword)
        );

        resetToken.setUsed(true);

        userRepository.save(user);
        tokenRepository.save(resetToken);
    }
}