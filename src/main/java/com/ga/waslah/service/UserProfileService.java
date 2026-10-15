package com.ga.waslah.service;

import com.ga.waslah.dto.UserProfileRequest;
import com.ga.waslah.exception.ResourceNotFoundException;
import com.ga.waslah.model.User;
import com.ga.waslah.model.UserProfile;
import com.ga.waslah.repository.UserProfileRepository;
import com.ga.waslah.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserProfileService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    public UserProfileService(
            UserRepository userRepository,
            UserProfileRepository userProfileRepository
    ) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
    }

    public UserProfile getProfile(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        if (user.getUserProfile() == null) {
            throw new ResourceNotFoundException("Profile not found");
        }

        return user.getUserProfile();
    }

    public UserProfile updateProfile(
            String username,
            UserProfileRequest request
    ) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        UserProfile profile = user.getUserProfile();

        if (profile == null) {
            profile = new UserProfile();
            user.setUserProfile(profile);
        }

        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setPhone(request.getPhone());
        profile.setBio(request.getBio());
        profile.setLocation(request.getLocation());

        userRepository.save(user);

        return user.getUserProfile();
    }

    public UserProfile updateProfileImage(
            String username,
            MultipartFile image
    ) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        UserProfile profile = user.getUserProfile();

        if (profile == null) {
            throw new ResourceNotFoundException("Profile not found");
        }

        profile.setProfileImage(image.getOriginalFilename());

        return userProfileRepository.save(profile);
    }
}