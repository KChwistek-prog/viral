package com.med.viral.service.serviceImpl;

import com.med.viral.exceptions.UserNotFoundException;
import com.med.viral.model.Action;
import com.med.viral.model.ActionType;
import com.med.viral.model.Admin;
import com.med.viral.model.DTO.AdminDTO;
import com.med.viral.model.DTO.DoctorDTO;
import com.med.viral.model.DTO.PatientDTO;
import com.med.viral.model.Patient;
import com.med.viral.model.mapper.UserMapper;
import com.med.viral.model.security.ChangePasswordRequest;
import com.med.viral.model.security.Role;
import com.med.viral.repository.ActionRepository;
import com.med.viral.repository.AdminRepository;
import com.med.viral.repository.DoctorRepository;
import com.med.viral.repository.PatientRepository;
import com.med.viral.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final PatientRepository patientRepository;
    private final UserMapper userMapper;
    private final DoctorRepository doctorRepository;
    private final AdminRepository adminRepository;
    private final Clock clock;
    private final ActionRepository actionRepository;

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

    public ResponseEntity<PatientDTO> editUser(Integer id, PatientDTO patientDTO) throws Exception {
        var loggedAdmin = (Admin) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var user = patientRepository.findById(id).stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("User could not be found."));

       userMapper.updateUserFromDto(patientDTO, user);

        var action = Action.builder()
                .actionType(ActionType.MODIFY_ACCOUNT)
                .createdBy(loggedAdmin.getId())
                .createdDate(LocalDateTime.now(clock))
                .fieldName(patientDTO.toString())
                .oldVersion("false")
                .newVersion(patientDTO.toString())
                .build();

        actionRepository.save(action);

        return ResponseEntity.ok(userMapper.PatientEntityToDTO(patientRepository.save(user)));
    }


    @Override
    public void deleteUser(Integer userIdToDelete) throws Exception {
        var loggedAdmin = (Admin) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var user = patientRepository.findAll().stream()
                .filter(u -> u.getId().equals(userIdToDelete))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("User could not be found."));

        switch (user.getRole()) {
            case Role.ADMIN -> throw new Exception("Can't delete administrator account");
            case Role.PATIENT -> patientRepository.deleteById(user.getId());
            case Role.DOCTOR -> doctorRepository.deleteById(user.getId());
        }

        Action action = Action.builder()
                .actionType(ActionType.DELETE_ACCOUNT)
                .createdBy(loggedAdmin.getId())
                .createdDate(LocalDateTime.now(clock))
                .fieldName("User account")
                .oldVersion(user.getUsername())
                .newVersion("Deleted")
                .build();

        actionRepository.save(action);
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
