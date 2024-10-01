package com.med.viral.service;

import com.med.viral.exceptions.UserNotFoundException;
import com.med.viral.model.Appointment;
import com.med.viral.model.AppointmentStatus;
import com.med.viral.model.DTO.AdminDTO;
import com.med.viral.model.DTO.DoctorDTO;
import com.med.viral.model.DTO.UserDTO;
import com.med.viral.model.User;
import com.med.viral.model.mapper.UserMapper;
import com.med.viral.model.security.ChangePasswordRequest;
import com.med.viral.model.security.Role;
import com.med.viral.repository.AdminRepository;
import com.med.viral.repository.AppointmentRepository;
import com.med.viral.repository.DoctorRepository;
import com.med.viral.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final AdminRepository adminRepository;

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

    public UserDTO saveUser(UserDTO userDTO) {
        User user = userMapper.UserDTOtoEntity(userDTO);
        return userMapper.UserEntityToDTO(userRepository.save(user));
    }

    public void deleteUser(UserDTO userDTO) throws UserNotFoundException {
        var user = userRepository.findAll().stream()
                .filter(u -> u.getId().equals(userDTO.id()))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("User could not be found."));
        userRepository.delete(user);
    }

    public UserDTO getByEmail(String email) throws UserNotFoundException {
        var user = userRepository.findAll().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("User with the provided email could not be found."));
        return userMapper.UserEntityToDTO(user);
    }

    public UserDTO getById(Integer id) throws UserNotFoundException {
        var user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User with the provided ID could not be found."));
        return userMapper.UserEntityToDTO(user);
    }

    public UserDTO getUserByUsername(String username) throws UserNotFoundException {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with the provided username could not be found."));
        return userMapper.UserEntityToDTO(user);
    }

    public DoctorDTO getDoctorByUsername(String username) throws UserNotFoundException {
        var doctor = doctorRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with the provided username could not be found."));
        return userMapper.DoctorEntityToDTO(doctor);
    }

    public AdminDTO getAdminByUsername(String username) throws UserNotFoundException {
        var admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with the provided username could not be found."));
        return userMapper.AdminEntityToDTO(admin);
    }

    public List<UserDTO> getAllNonAdminUsers() {
        return userRepository.findAll().stream()
                .filter(u -> !u.getRole().equals(Role.ADMIN))
                .map(userMapper::UserEntityToDTO)
                .toList();
    }

    public Set<UserDTO> getAllPatients(Integer doctorId) {
        return appointmentRepository.findAll().stream()
                .filter(a -> a.getDoctor().getId().equals(doctorId))
                .map(Appointment::getUser)
                .map(userMapper::UserEntityToDTO)
                .collect(Collectors.toSet());
    }

    public Appointment registerAppointment(User user, Integer doctorId) throws UserNotFoundException {
        var doctor = doctorRepository.findById(doctorId);
        if (user.isAccountNonLocked()) {
            return appointmentRepository.save(Appointment.builder()
                    .user(user)
                    .doctor(doctor.orElseThrow())
                    .date(new Date())
                    .status(AppointmentStatus.OPEN)
                    .build());
        } else throw new UserNotFoundException("User Locked");

    }
}