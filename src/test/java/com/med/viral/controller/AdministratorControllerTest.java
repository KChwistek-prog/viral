package com.med.viral.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.med.viral.model.Admin;
import com.med.viral.model.Patient;
import com.med.viral.model.User;
import com.med.viral.model.mapper.UserMapper;
import com.med.viral.model.security.AuthenticationRequest;
import com.med.viral.model.security.AuthenticationResponse;
import com.med.viral.model.security.Role;
import com.med.viral.repository.AdminRepository;
import com.med.viral.repository.TokenRepository;
import com.med.viral.repository.PatientRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AdministratorControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AdminRepository adminRepository;

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        Admin admin = new Admin();
        admin.setFirstname("John");
        admin.setLastname("Doe");
        admin.setUsername("admin1");
        admin.setPassword(passwordEncoder.encode("1234"));
        admin.setEmail("johndoe1@example.com");
        admin.setPesel(1234567890L);
        admin.setRole(Role.ADMIN);
        admin.setAccountNonLocked(true);
        adminRepository.save(admin);

        var user = new Patient();
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setUsername("user1");
        user.setPassword(passwordEncoder.encode("1234"));
        user.setEmail("johndoe2@example.com");
        user.setPesel(1234567890L);
        user.setRole(Role.PATIENT);
        user.setAccountNonLocked(false);
        patientRepository.save(user);
    }

    @AfterEach
    void cleanUp() {
        tokenRepository.deleteAll();
        adminRepository.deleteAll();
        patientRepository.deleteAll();
    }


    @Test
    void addAccount() throws Exception {
        //given
        var user = new Patient();
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setUsername("user3");
        user.setPassword(passwordEncoder.encode("1234"));
        user.setEmail("johndoe3@example.com");
        user.setPesel(1234567890L);
        user.setRole(Role.PATIENT);
        user.setAccountNonLocked(false);
        var login = new AuthenticationRequest("admin1", "1234", Role.ADMIN);
        var loggedAdmin = mockMvc.perform(post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login))).andReturn().getResponse().getContentAsString();
        var token = objectMapper.readValue(loggedAdmin, AuthenticationResponse.class);
        //when and then
        mockMvc.perform(put("/administrator/addAccount")
                        .header("Authorization", "Bearer " + token.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userMapper.PatientEntityToDTO(user))))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUserAccount() throws Exception {
        //given
        var login = new AuthenticationRequest("admin1", "1234", Role.ADMIN);
        var loggedAdmin = mockMvc.perform(post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login))).andReturn().getResponse().getContentAsString();
        var token = objectMapper.readValue(loggedAdmin, AuthenticationResponse.class);
        User userToDelete = patientRepository.findByUsername("user1").orElseThrow();
        //when and then
        mockMvc.perform(delete("/administrator/deleteAccount/" + userToDelete.getId())
                        .header("Authorization", "Bearer " + token.accessToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void editUserAccount() throws Exception {
        //given
        var userEdit = new Patient();
        userEdit.setFirstname("John");
        userEdit.setLastname("Rambo");
        userEdit.setRole(Role.PATIENT);
        var login = new AuthenticationRequest("admin1", "1234", Role.ADMIN);
        var loggedAdmin = mockMvc.perform(post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login))).andReturn().getResponse().getContentAsString();
        var token = objectMapper.readValue(loggedAdmin, AuthenticationResponse.class);
        var userToModify = patientRepository.findByUsername("user1").orElseThrow();
        //when and then
        mockMvc.perform(patch("/administrator/updateUser/" + userToModify.getId())
                        .header("Authorization", "Bearer " + token.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userMapper.PatientEntityToDTO(userEdit))))
                .andExpect(status().isOk());
    }

    @Test
    void changeAccountStatus() throws Exception {
        //given
        var login = new AuthenticationRequest("admin1", "1234", Role.ADMIN);
        var loggedAdmin = mockMvc.perform(post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login))).andReturn().getResponse().getContentAsString();
        var token = objectMapper.readValue(loggedAdmin, AuthenticationResponse.class);
        //when and then
        mockMvc.perform(post("/administrator/changeAccountStatus/user1")
                        .header("Authorization", "Bearer " + token.accessToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void listAllUsers() throws Exception {
        //given
        var login = new AuthenticationRequest("admin1", "1234", Role.ADMIN);
        var loggedAdmin = mockMvc.perform(post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login))).andReturn().getResponse().getContentAsString();
        var token = objectMapper.readValue(loggedAdmin, AuthenticationResponse.class);
        //when and then
        mockMvc.perform(get("/administrator/userList")
                        .header("Authorization", "Bearer " + token.accessToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}