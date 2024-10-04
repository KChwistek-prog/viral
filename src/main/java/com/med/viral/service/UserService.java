package com.med.viral.service;

import com.med.viral.exceptions.UserNotFoundException;
import com.med.viral.model.DTO.AdminDTO;
import com.med.viral.model.DTO.DoctorDTO;
import com.med.viral.model.DTO.UserDTO;
import com.med.viral.model.security.ChangePasswordRequest;

import java.security.Principal;
import java.util.List;


public interface UserService {
    void changePassword(ChangePasswordRequest request, Principal connectedUser);

    UserDTO saveUser(UserDTO userDTO);

    void deleteUser(UserDTO userDTO) throws UserNotFoundException;

    UserDTO getByEmail(String email) throws UserNotFoundException;

    UserDTO getById(Integer id) throws UserNotFoundException;

    UserDTO getUserByUsername(String username) throws UserNotFoundException;

    DoctorDTO getDoctorByUsername(String username) throws UserNotFoundException;

    AdminDTO getAdminByUsername(String username) throws UserNotFoundException;

    List<UserDTO> getAllNonAdminUsers();
}