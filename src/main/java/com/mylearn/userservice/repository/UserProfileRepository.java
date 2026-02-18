package com.mylearn.userservice.repository;

import com.mylearn.userservice.model.UserProfile;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Spring Data JPA Repository for the UserProfile entity. */
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {

  Optional<UserProfile> findByUserId(UUID userId);
}
