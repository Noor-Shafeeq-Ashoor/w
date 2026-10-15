package com.ga.waslah.service;

import com.ga.waslah.dto.JobRequest;
import com.ga.waslah.exception.ForbiddenException;
import com.ga.waslah.model.Role;
import com.ga.waslah.model.User;
import com.ga.waslah.repository.JobRepository;
import com.ga.waslah.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private JobService jobService;


    @Test
    void shouldRejectJobCreationForRegularUser() {

        // Arrange
        User user = new User();
        user.setUsername("noor");
        user.setRole(Role.USER);

        JobRequest request = new JobRequest();

        when(userRepository.findByUsername("noor"))
                .thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(
                ForbiddenException.class,
                () -> jobService.createJob("noor", request)
        );

        // Make sure no job was saved
        verify(jobRepository, never()).save(any());
    }
}