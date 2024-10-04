package com.med.viral.service.serviceImpl;

import com.med.viral.exceptions.UserNotFoundException;
import com.med.viral.model.DTO.AdminDTO;
import com.med.viral.model.DTO.DoctorDTO;
import com.med.viral.model.DTO.PatientDTO;
import com.med.viral.model.Patient;
import com.med.viral.model.User;
import com.med.viral.model.mapper.UserMapper;
import com.med.viral.model.security.ChangePasswordRequest;
import com.med.viral.model.security.Role;
import com.med.viral.repository.AdminRepository;
import com.med.viral.repository.DoctorRepository;
import com.med.viral.repository.PatientRepository;
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
    private final PatientRepository patientRepository;
    private final UserMapper userMapper;
    private final DoctorRepository doctorRepository;
    private final AdminRepository adminRepository;

    @Override
    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {
        var user = (Patient) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong password");
        }
        if (!request.newPassword().equals(request.confirmationPassword())) {
            throw new IllegalStateException("Password are not the same");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        patientRepository.save(user);
    }

    @Override
    public PatientDTO saveUser(PatientDTO patientDTO) {
        var user = userMapper.PatientDTOtoEntity(patientDTO);
        return userMapper.PatientEntityToDTO(patientRepository.save(user));
    }

    @Override
    public void deleteUser(PatientDTO patientDTO) throws UserNotFoundException {
        var user = patientRepository.findAll().stream()
                .filter(u -> u.getId().equals(patientDTO.id()))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("User could not be found."));
        patientRepository.delete(user);
    }

    @Override
    public PatientDTO getByEmail(String email) throws UserNotFoundException {
        var user = patientRepository.findAll().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("User with the provided email could not be found."));
        return userMapper.PatientEntityToDTO(user);
    }

    @Override
    public PatientDTO getById(Integer id) throws UserNotFoundException {
        var user = patientRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User with the provided ID could not be found."));
        return userMapper.PatientEntityToDTO(user);
    }

    @Override
    public PatientDTO getUserByUsername(String username) throws UserNotFoundException {
        var user = patientRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with the provided username could not be found."));
        return userMapper.PatientEntityToDTO(user);
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
    public List<PatientDTO> getAllNonAdminUsers() {
        return patientRepository.findAll().stream()
                .filter(u -> !u.getRole().equals(Role.ADMIN))
                .map(userMapper::PatientEntityToDTO)
                .toList();
    }
}
