package com.med.viral.service.serviceImpl;

import com.med.viral.model.Admin;
import com.med.viral.model.Doctor;
import com.med.viral.model.User;
import com.med.viral.repository.AdminRepository;
import com.med.viral.repository.DoctorRepository;
import com.med.viral.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;

    public UserDetails loadUserByUsername(String username) {
        Optional<Admin> admin = adminRepository.findByUsername(username);
        if (admin.isPresent()) {
            return admin.get();
        }

        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            return user.get();
        }

        Optional<Doctor> doctor = doctorRepository.findByUsername(username);
        return doctor.orElse(null);
    }

}
