package com.med.viral.service;

import com.med.viral.exceptions.UserNotFoundException;
import com.med.viral.model.DTO.UserDTO;
import com.med.viral.model.User;
import com.med.viral.model.mapper.UserMapper;
import com.med.viral.model.security.ChangePasswordRequest;
import com.med.viral.model.security.Role;
import com.med.viral.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;
    private final UserMapper userMapper;

    public UserService(PasswordEncoder passwordEncoder, UserRepository repository, UserMapper userMapper) {
        this.passwordEncoder = passwordEncoder;
        this.repository = repository;
        this.userMapper = userMapper;
    }

    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong password");
        }
        if (!request.newPassword().equals(request.confirmationPassword())) {
            throw new IllegalStateException("Password are not the same");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        repository.save(user);
    }

    public UserDTO saveUser(UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        return userMapper.toDTO(repository.save(user));
    }

    public void deleteUser(UserDTO userDTO) throws UserNotFoundException {
        var user = repository.findAll().stream()
                .filter(u -> u.getId().equals(userDTO.id()))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("User could not be found."));
        repository.delete(user);
    }

    public UserDTO getByEmail(String email) throws UserNotFoundException {
        var user = repository.findAll().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("User with the provided email could not be found."));
        return userMapper.toDTO(user);
    }

    public UserDTO getById(Integer id) throws UserNotFoundException {
        var user = repository.findById(id).orElseThrow(() -> new UserNotFoundException("User with the provided ID could not be found."));
        return userMapper.toDTO(user);
    }

    public UserDTO getByUsername(String username) throws UserNotFoundException {
        var user = repository.findAll().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("User with the provided email could not be found."));
        return userMapper.toDTO(user);
    }

    public List<UserDTO> getAllNonAdminUsers() {
        return repository.findAll().stream()
                .filter(u -> !u.getRole().equals(Role.ADMIN))
                .map(userMapper::toDTO)
                .toList();
    }

    public List<UserDTO> getAllPatientsByIds(Set<Integer> patientList) {
        var user = repository.findAllById(patientList);
        return  user.stream().map(userMapper::toDTO).toList();
    }
}