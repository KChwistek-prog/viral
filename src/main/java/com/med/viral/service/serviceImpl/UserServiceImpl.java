package com.med.viral.service.serviceImpl;

import com.med.viral.exceptions.UserNotFoundException;
import com.med.viral.model.DTO.AdminDTO;
import com.med.viral.model.DTO.DoctorDTO;
import com.med.viral.model.DTO.UserDTO;
import com.med.viral.model.User;
import com.med.viral.model.mapper.UserMapper;
import com.med.viral.model.security.ChangePasswordRequest;
import com.med.viral.model.security.Role;
import com.med.viral.repository.AdminRepository;
import com.med.viral.repository.DoctorRepository;
import com.med.viral.repository.UserRepository;
import com.med.viral.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final DoctorRepository doctorRepository;
    private final AdminRepository adminRepository;

    @Override
    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong password");
        }
        if (!request.newPassword().equals(request.confirmationPassword())) {
            throw new IllegalStateException("Password are not the same");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    @Override
    public UserDTO saveUser(UserDTO userDTO) {
        User user = userMapper.UserDTOtoEntity(userDTO);
        return userMapper.UserEntityToDTO(userRepository.save(user));
    }

    @Override
    public void deleteUser(UserDTO userDTO) throws UserNotFoundException {
        var user = userRepository.findAll().stream()
                .filter(u -> u.getId().equals(userDTO.id()))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("User could not be found."));
        userRepository.delete(user);
    }

    @Override
    public UserDTO getByEmail(String email) throws UserNotFoundException {
        var user = userRepository.findAll().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("User with the provided email could not be found."));
        return userMapper.UserEntityToDTO(user);
    }

    @Override
    public UserDTO getById(Integer id) throws UserNotFoundException {
        var user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User with the provided ID could not be found."));
        return userMapper.UserEntityToDTO(user);
    }

    @Override
    public UserDTO getUserByUsername(String username) throws UserNotFoundException {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with the provided username could not be found."));
        return userMapper.UserEntityToDTO(user);
    }

    @Override
    public DoctorDTO getDoctorByUsername(String username) throws UserNotFoundException {
        var doctor = doctorRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with the provided username could not be found."));
        return userMapper.DoctorEntityToDTO(doctor);
    }

    @Override
    public AdminDTO getAdminByUsername(String username) throws UserNotFoundException {
        var admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with the provided username could not be found."));
        return userMapper.AdminEntityToDTO(admin);
    }

    @Override
    public List<UserDTO> getAllNonAdminUsers() {
        return userRepository.findAll().stream()
                .filter(u -> !u.getRole().equals(Role.ADMIN))
                .map(userMapper::UserEntityToDTO)
                .toList();
    }
}
