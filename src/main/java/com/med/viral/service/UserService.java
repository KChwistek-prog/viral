package com.med.viral.service;

import com.med.viral.exceptions.UserNotFoundException;
import com.med.viral.model.DTO.UserDTO;
import com.med.viral.model.User;
import com.med.viral.model.mapper.UserMapper;
import com.med.viral.model.security.ChangePasswordRequest;
import com.med.viral.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;
    private final UserMapper userMapper;

    @Autowired
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

}