package com.med.viral.repository;

import com.med.viral.model.User;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findById(Integer id);

    boolean existsByUsername(@NotNull(message = "Username is required") String username);

}
