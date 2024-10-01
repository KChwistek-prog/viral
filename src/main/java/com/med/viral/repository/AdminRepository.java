package com.med.viral.repository;

import com.med.viral.model.Admin;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
    Optional<Admin> findByUsername(String username);

    boolean existsByUsername(@NotNull(message = "Username is required") String username);
}
