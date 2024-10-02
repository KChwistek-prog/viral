package com.med.viral.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.med.viral.model.Admin;
import com.med.viral.model.User;
import com.med.viral.model.mapper.UserMapper;
import com.med.viral.model.security.AuthenticationRequest;
import com.med.viral.model.security.AuthenticationResponse;
import com.med.viral.model.security.Role;
import com.med.viral.repository.AdminRepository;
import com.med.viral.repository.DoctorRepository;
import com.med.viral.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdministratorControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AdminRepository adminRepository;

    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    void deleteUserAccount() {
    }

    @Test
    void editUserAccount() {
    }

    @Test
    void addAccount() throws Exception {
        //given
        Admin admin = new Admin();
        admin.setFirstname("John");
        admin.setLastname("Doe");
        admin.setUsername("admin1");
        admin.setPassword(passwordEncoder.encode("1234"));
        admin.setEmail("johndoe@example.com");
        admin.setPesel(1234567890);
        admin.setRole(Role.ADMIN);
        admin.setAccountNonLocked(true);
        adminRepository.save(admin);

        User user = new User();
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setUsername("user");
        user.setPassword(passwordEncoder.encode("1234"));
        user.setEmail("johndoe@example.com");
        user.setPesel(1234567890);
        user.setRole(Role.PATIENT);
        user.setAccountNonLocked(true);

        var login = new AuthenticationRequest("admin1", "1234", Role.ADMIN);
        var loggedAdmin = mockMvc.perform(post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login))).andReturn().getResponse().getContentAsString();
        var token = objectMapper.readValue(loggedAdmin, AuthenticationResponse.class);

        mockMvc.perform(put("/administrator/addAccount")
                .header("Authorization","Bearer "+ token.accessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userMapper.UserEntityToDTO(user))))
                .andExpect(status().isOk());

    }

    @Test
    void changeAppointmentStatus() {
    }

    @Test
    void changeAccountStatus() {
    }

    @Test
    void cancelAppointment() {
    }

    @Test
    void listAllUsers() {
    }

    @Test
    void listAllActions() {
    }
}