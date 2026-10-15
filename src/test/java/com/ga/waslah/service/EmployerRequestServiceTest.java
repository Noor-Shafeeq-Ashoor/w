package com.ga.waslah.service;

import com.ga.waslah.model.*;
import com.ga.waslah.repository.EmployerRequestRepository;
import com.ga.waslah.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployerRequestServiceTest {

    @Mock
    private EmployerRequestRepository employerRequestRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EmployerRequestService employerRequestService;


    @Test
    void shouldApproveEmployerRequestAndChangeUserRole() {

        // Arrange
        User user = new User();
        user.setUsername("noor");
        user.setRole(Role.USER);

        EmployerRequest request = new EmployerRequest();

        request.setId(1L);
        request.setUser(user);
        request.setStatus(EmployerRequestStatus.PENDING);

        when(employerRequestRepository.findById(1L))
                .thenReturn(Optional.of(request));

        when(userRepository.save(user))
                .thenReturn(user);

        when(employerRequestRepository.save(request))
                .thenReturn(request);

        // Act
        EmployerRequest result =
                employerRequestService.approveRequest(1L);

        // Assert
        assertEquals(
                EmployerRequestStatus.APPROVED,
                result.getStatus()
        );

        assertEquals(
                Role.EMPLOYER,
                user.getRole()
        );

        verify(userRepository).save(user);
        verify(employerRequestRepository).save(request);
    }
}