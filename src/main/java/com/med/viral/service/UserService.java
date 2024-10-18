package com.med.viral.service;

import com.med.viral.exceptions.UserNotFoundException;
import com.med.viral.model.DTO.AdminDTO;
import com.med.viral.model.DTO.DoctorDTO;
import com.med.viral.model.DTO.PatientDTO;
import com.med.viral.model.security.ChangePasswordRequest;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

public interface UserService {
    void changePassword(ChangePasswordRequest request, Principal connectedUser);

    PatientDTO saveUser(PatientDTO patientDTO);

    void deleteUser(Integer userIdToDelete) throws Exception;

    PatientDTO getByEmail(String email) throws UserNotFoundException;

    PatientDTO getById(Integer id) throws UserNotFoundException;

    PatientDTO getUserByUsername(String username) throws UserNotFoundException;

    DoctorDTO getDoctorByUsername(String username) throws UserNotFoundException;

    AdminDTO getAdminByUsername(String username) throws UserNotFoundException;

    List<PatientDTO> getAllNonAdminUsers();

    ResponseEntity<PatientDTO> editUser(Integer id, PatientDTO patientDTO) throws Exception;
}