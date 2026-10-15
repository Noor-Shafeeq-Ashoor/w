package com.ga.waslah.service;

import com.ga.waslah.dto.ChangePasswordRequest;
import com.ga.waslah.exception.BadRequestException;
import com.ga.waslah.model.User;
import com.ga.waslah.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailVerificationService emailVerificationService;

    @InjectMocks
    private UserService userService;


    @Test
    void shouldLockUserAfterThreeFailedLoginAttempts() {

        User user = new User();

        user.setFailedLoginAttempts(2);
        user.setLockedUntil(null);

        userService.recordFailedLogin(user);

        assertEquals(3, user.getFailedLoginAttempts());
        assertNotNull(user.getLockedUntil());
        assertTrue(
                user.getLockedUntil().isAfter(LocalDateTime.now())
        );

        verify(userRepository).save(user);
    }
    @Test
    void shouldRejectPasswordChangeWhenCurrentPasswordIsIncorrect() {

        User user = new User();
        user.setUsername("noor");
        user.setPassword("encodedOldPassword");

        when(userRepository.findByUsername("noor"))
                .thenReturn(java.util.Optional.of(user));

        when(passwordEncoder.matches(
                "wrongPassword",
                "encodedOldPassword"
        )).thenReturn(false);

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("wrongPassword");
        request.setNewPassword("newPassword123");

        assertThrows(
                BadRequestException.class,
                () -> userService.changePassword("noor", request)
        );

        verify(userRepository, never()).save(user);
    }

}



