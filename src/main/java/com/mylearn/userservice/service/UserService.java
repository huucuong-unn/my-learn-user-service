package com.mylearn.userservice.service;

import com.mylearn.common.dto.user.InternalProfileCreationRequest;
import com.mylearn.common.dto.user.UserProfileResponse;
import com.mylearn.common.dto.user.UserProfileUpdateRequest;
import com.mylearn.userservice.exception.ResourceNotFoundException;
import com.mylearn.userservice.model.UserProfile;
import com.mylearn.userservice.repository.UserProfileRepository;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserProfileRepository userProfileRepository;

  public UserProfileResponse createProfile(InternalProfileCreationRequest request) {
    log.trace("Creating profile for new user ID: {}", request.getUserId());

    UserProfileResponse userProfileResponse = fetchProfileByUserId(request.getUserId());

    // Defensive check: Should not happen if auth-service is the source, but prevents data
    // duplication.
    if (Objects.nonNull(userProfileResponse)) {
      log.warn("Profile already exists for user ID: {}", request.getUserId());
      return userProfileResponse;
    }

    UserProfile userProfile = new UserProfile();
    userProfile.setUserId(request.getUserId());
    userProfile.setFullName(request.getFullName());
    userProfile.setRole(request.getRole());
    userProfile.setBio("A new " + request.getRole().name().toLowerCase() + " on MyLearn.");

    UserProfile savedProfile = userProfileRepository.save(userProfile);

    return toResponse(savedProfile);
  }

  public UserProfileResponse fetchProfileByUserId(UUID userId) {
    log.info("Fetching user profile for user ID: {}", userId);

    UserProfile profile =
        userProfileRepository
            .findById(userId)
            .orElse(null);

    return toResponse(profile);
  }

  public UserProfileResponse updateProfile(UUID userId, UserProfileUpdateRequest request) {
    log.info("Updating user profile for user ID: {}", userId);

    UserProfile profile =
        userProfileRepository
            .findById(userId)
            .orElseThrow(
                () -> new ResourceNotFoundException("User profile not found for ID: " + userId));

    if (Objects.nonNull(request.getFullName())) {
      profile.setFullName(request.getFullName());
    }

    if (Objects.nonNull(request.getBio())) {
      profile.setBio(request.getBio());
    }

    if (Objects.nonNull(request.getProfilePictureUrl())) {
      profile.setProfilePictureUrl(request.getProfilePictureUrl());
    }

    UserProfile updatedProfile = userProfileRepository.save(profile);

    return toResponse(updatedProfile);
  }

  /** Converts the JPA Entity to the public DTO response. */
  private UserProfileResponse toResponse(UserProfile profile) {
    return Objects.isNull(profile)
        ? null
        : UserProfileResponse.builder()
            .userId(profile.getUserId())
            .fullName(profile.getFullName())
            .bio(profile.getBio())
            .profilePictureUrl(profile.getProfilePictureUrl())
            .role(profile.getRole())
            .build();
  }
}
