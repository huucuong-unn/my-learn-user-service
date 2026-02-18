package com.mylearn.userservice.controller;

import com.mylearn.common.dto.user.UserProfileResponse;
import com.mylearn.common.dto.user.UserProfileUpdateRequest;
import com.mylearn.userservice.service.UserService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

  private final UserService userService;

  // NOTE: In a real environment, the user ID below would be extracted from the
  // JWT token validated by the API Gateway and passed in a special header
  // (e.g., 'X-User-Id' or 'X-User-Role').
  private static final String MOCK_USER_ID_HEADER = "X-User-Id";

  /** GET /api/v1/users/me Retrieves the authenticated user's profile. */
  @GetMapping("/me")
  public ResponseEntity<UserProfileResponse> getMyProfile(
      @RequestHeader(MOCK_USER_ID_HEADER) String userId) {

    UUID authenticatedUserId = UUID.fromString(userId);
    log.info("Fetching /me profile for user ID: {}", authenticatedUserId);

    UserProfileResponse response = userService.fetchProfileByUserId(authenticatedUserId);

    return ResponseEntity.ok(response);
  }

  /** PUT /api/v1/users/me Updates the authenticated user's profile information. */
  @PutMapping("/me")
  public ResponseEntity<UserProfileResponse> updateMyProfile(
      @RequestHeader(MOCK_USER_ID_HEADER) String userIdHeader,
      @RequestBody UserProfileUpdateRequest request) {

    UUID authenticatedUserId = parseUserId(userIdHeader);
    log.info("Updating /me profile for user ID: {}", authenticatedUserId);

    UserProfileResponse response = userService.updateProfile(authenticatedUserId, request);
    return ResponseEntity.ok(response);
  }

  /**
   * GET /api/v1/users/{userId} Retrieves a public profile for a specific user (e.g., viewing an
   * instructor).
   */
  @GetMapping("/{userId}")
  public ResponseEntity<UserProfileResponse> getProfileById(@PathVariable UUID userId) {
    log.info("Fetching public profile for user ID: {}", userId);

    UserProfileResponse response = userService.fetchProfileByUserId(userId);
    return ResponseEntity.ok(response);
  }

  private UUID parseUserId(String userIdHeader) {
    try {
      return UUID.fromString(userIdHeader);
    } catch (IllegalArgumentException e) {
      log.error("Invalid UUID format received in header {}: {}", MOCK_USER_ID_HEADER, userIdHeader);
      // In a production environment, this should ideally be handled by the security filter
      // returning 401/403 before it hits the controller.
      throw new IllegalArgumentException("Invalid user ID format.");
    }
  }
}
