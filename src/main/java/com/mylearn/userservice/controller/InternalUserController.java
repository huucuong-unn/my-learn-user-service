package com.mylearn.userservice.controller;

import com.mylearn.common.dto.user.InternalProfileCreationRequest;
import com.mylearn.common.dto.user.UserProfileResponse;
import com.mylearn.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Internal REST Controller for service-to-service calls (e.g., from auth-service). These endpoints
 * should be strictly protected in a production environment (e.g., via Kubernetes NetworkPolicy or
 * API Gateway rules) as they are administrative commands.
 */
@RestController
@RequestMapping("/internal/profiles") // Distinct path for internal calls
@RequiredArgsConstructor
@Slf4j
public class InternalUserController {

  private final UserService userService;

  /**
   * POST /internal/profiles Creates a new user profile upon successful registration in the
   * auth-service.
   */
  @PostMapping
  public ResponseEntity<UserProfileResponse> createProfile(
      @RequestBody InternalProfileCreationRequest request) {
    log.info("Internal request received to create profile for user ID: {}", request.getUserId());

    UserProfileResponse response = userService.createProfile(request);

    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }
}
