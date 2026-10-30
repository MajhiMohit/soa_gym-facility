package com.pulsefit.auth.repository;

import com.pulsefit.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for User entity.
 * Provides JPA-based data access for authentication operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by their username for login authentication.
     */
    Optional<User> findByUsername(String username);

    /**
     * Check if a username already exists during registration.
     */
    boolean existsByUsername(String username);
}
