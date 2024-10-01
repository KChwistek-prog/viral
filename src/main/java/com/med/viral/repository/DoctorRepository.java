package com.med.viral.repository;

import com.med.viral.model.Doctor;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Integer> {
    Optional<Doctor> findByUsername(String username);

    boolean existsByUsername(@NotNull(message = "Username is required") String username);
}
