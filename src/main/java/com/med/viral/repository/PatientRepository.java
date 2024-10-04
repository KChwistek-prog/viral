package com.med.viral.repository;

import com.med.viral.model.Patient;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Integer> {
    Optional<Patient> findByUsername(String username);

    Optional<Patient> findByEmail(String email);

    Optional<Patient> findById(Integer id);

    boolean existsByUsername(@NotNull(message = "Username is required") String username);

}
