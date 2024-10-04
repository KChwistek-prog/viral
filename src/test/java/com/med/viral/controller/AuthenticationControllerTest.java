package com.med.viral.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.med.viral.model.Admin;
import com.med.viral.model.Doctor;
import com.med.viral.model.Patient;
import com.med.viral.model.User;
import com.med.viral.model.mapper.UserMapper;
import com.med.viral.model.security.AuthenticationRequest;
import com.med.viral.model.security.RegisterRequest;
import com.med.viral.model.security.Role;
import com.med.viral.repository.AdminRepository;
import com.med.viral.repository.DoctorRepository;
import com.med.viral.repository.PatientRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthenticationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AdminRepository adminRepository;

    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    void testRegister() throws Exception {
        //given
        RegisterRequest request = new RegisterRequest("Alice", "Cooper", "testuser", "password123", Role.ADMIN);
        //when and then
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

    }

    @Test
    public void testRegisterWithNull() throws Exception {
        //given
        RegisterRequest request = new RegisterRequest("Alice", "Cooper", null, "password123", Role.ADMIN);
        //when and then
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testPatientAuthenticate() throws Exception {
        //given
        var user = new Patient();
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setUsername("patient");
        user.setPassword(passwordEncoder.encode("1234"));
        user.setEmail("johndoe@example.com");
        user.setPesel(1234567890L);
        user.setRole(Role.PATIENT);
        user.setAccountNonLocked(true);
        patientRepository.save(user);

        //when and then
        var login = new AuthenticationRequest("patient", "1234", Role.PATIENT);
        mockMvc.perform(post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk());
    }

    @Test
    public void testDoctorAuthenticate() throws Exception {
        //given
        var doctor = new Doctor();
        doctor.setFirstname("John");
        doctor.setLastname("Doe");
        doctor.setUsername("doctor");
        doctor.setPassword(passwordEncoder.encode("1234"));
        doctor.setEmail("johndoe2@example.com");
        doctor.setPesel(1234567890L);
        doctor.setRole(Role.DOCTOR);
        doctor.setAccountNonLocked(true);
        doctorRepository.save(doctor);

        //when and then
        var login = new AuthenticationRequest("doctor", "1234", Role.DOCTOR);
        mockMvc.perform(post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk());
    }

    @Test
    public void testAdminAuthenticate() throws Exception {
        //given
        var admin = new Admin();
        admin.setFirstname("John");
        admin.setLastname("Doe");
        admin.setUsername("admin1");
        admin.setPassword(passwordEncoder.encode("1234"));
        admin.setEmail("johndoe3@example.com");
        admin.setPesel(1234567890L);
        admin.setRole(Role.ADMIN);
        admin.setAccountNonLocked(true);
        adminRepository.save(admin);

        //when and then
        var login = new AuthenticationRequest("admin1", "1234", Role.ADMIN);
        mockMvc.perform(post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk());
    }
}