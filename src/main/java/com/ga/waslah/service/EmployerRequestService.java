package com.ga.waslah.service;

import com.ga.waslah.dto.EmployerRequestRequest;
import com.ga.waslah.exception.ConflictException;
import com.ga.waslah.exception.ResourceNotFoundException;
import com.ga.waslah.model.EmployerRequest;
import com.ga.waslah.model.EmployerRequestStatus;
import com.ga.waslah.model.Role;
import com.ga.waslah.model.User;
import com.ga.waslah.repository.EmployerRequestRepository;
import com.ga.waslah.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployerRequestService {

    private final EmployerRequestRepository employerRequestRepository;
    private final UserRepository userRepository;

    public EmployerRequest createRequest(
            String username,
            EmployerRequestRequest request
    ) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        // An existing employer does not need to submit a request.
        if (user.getRole() == Role.EMPLOYER) {
            throw new ConflictException(
                    "You are already an employer"
            );
        }

        var existingRequest =
                employerRequestRepository.findByUserUsername(username);

        if (existingRequest.isPresent()) {

            EmployerRequest oldRequest = existingRequest.get();

            if (oldRequest.getStatus() == EmployerRequestStatus.PENDING) {
                throw new ConflictException(
                        "You already have a pending employer request"
                );
            }

            if (oldRequest.getStatus() == EmployerRequestStatus.APPROVED) {
                throw new ConflictException(
                        "Your employer request has already been approved"
                );
            }

            // REJECTED → allow a new request.
            employerRequestRepository.delete(oldRequest);
        }

        EmployerRequest employerRequest = new EmployerRequest();

        employerRequest.setCompanyName(request.getCompanyName());
        employerRequest.setCompanyDescription(
                request.getCompanyDescription()
        );
        employerRequest.setJobTypes(request.getJobTypes());
        employerRequest.setCommercialRegistration(
                request.getCommercialRegistration()
        );
        employerRequest.setStatus(EmployerRequestStatus.PENDING);
        employerRequest.setUser(user);

        return employerRequestRepository.save(employerRequest);
    }

    public EmployerRequest getMyRequest(String username) {

        return employerRequestRepository
                .findByUserUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employer request not found"
                        ));
    }

    public List<EmployerRequest> getPendingRequests() {

        return employerRequestRepository.findByStatus(
                EmployerRequestStatus.PENDING
        );
    }

    public EmployerRequest approveRequest(Long requestId) {

        EmployerRequest request =
                employerRequestRepository.findById(requestId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Employer request not found"
                                ));

        if (request.getStatus() != EmployerRequestStatus.PENDING) {
            throw new ConflictException(
                    "Employer request has already been processed"
            );
        }

        request.setStatus(EmployerRequestStatus.APPROVED);

        User user = request.getUser();
        user.setRole(Role.EMPLOYER);

        userRepository.save(user);

        return employerRequestRepository.save(request);
    }

    public EmployerRequest rejectRequest(Long requestId) {

        EmployerRequest request =
                employerRequestRepository.findById(requestId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Employer request not found"
                                ));

        if (request.getStatus() != EmployerRequestStatus.PENDING) {
            throw new ConflictException(
                    "Employer request has already been processed"
            );
        }

        request.setStatus(EmployerRequestStatus.REJECTED);

        return employerRequestRepository.save(request);
    }
}
