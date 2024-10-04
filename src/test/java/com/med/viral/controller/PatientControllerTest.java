package com.med.viral.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.med.viral.model.Appointment;
import com.med.viral.model.AppointmentStatus;
import com.med.viral.model.Doctor;
import com.med.viral.model.User;
import com.med.viral.model.security.AuthenticationRequest;
import com.med.viral.model.security.AuthenticationResponse;
import com.med.viral.model.security.Role;
import com.med.viral.repository.AppointmentRepository;
import com.med.viral.repository.DoctorRepository;
import com.med.viral.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PatientControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    AppointmentRepository appointmentRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        User patient = new User();
        patient.setFirstname("John");
        patient.setLastname("Doe");
        patient.setUsername("patient1");
        patient.setPassword(passwordEncoder.encode("1234"));
        patient.setRole(Role.PATIENT);
        patient.setAccountNonLocked(true);
        userRepository.save(patient);

        Doctor doctor = new Doctor();
        doctor.setFirstname("James");
        doctor.setLastname("Smith");
        doctor.setUsername("doctor1");
        doctor.setPassword(passwordEncoder.encode("1234"));
        doctor.setRole(Role.DOCTOR);
        doctor.setAccountNonLocked(true);
        doctorRepository.save(doctor);
    }

    @Test
    void testAddAppointment() throws Exception {
        //given
        var doctor = doctorRepository.findByUsername("doctor1");
        var login = new AuthenticationRequest("patient1", "1234", Role.PATIENT);
        var loggedUser = mockMvc.perform(post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login))).andReturn().getResponse().getContentAsString();
        var token = objectMapper.readValue(loggedUser, AuthenticationResponse.class).accessToken();
        //when
        mockMvc.perform(post("/patient/addAppointment/" + doctor.orElseThrow().getId())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON));
        //then
        Assertions.assertEquals(1, appointmentRepository.findAll().size());
        Assertions.assertEquals(doctor.get().getId(), appointmentRepository.findAll().getFirst().getDoctor().getId());
    }

    @Test
    void testCancelAppointment() throws Exception {
        //given
        var login = new AuthenticationRequest("patient1", "1234", Role.PATIENT);
        var loggedAdmin = mockMvc.perform(post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login))).andReturn().getResponse().getContentAsString();
        var token = objectMapper.readValue(loggedAdmin, AuthenticationResponse.class).accessToken();
        var doctor = doctorRepository.findByUsername("doctor1").orElseThrow();
        var patient = userRepository.findByUsername("patient1").orElseThrow();
        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setUser(patient);
        appointment.setDate(new Date());
        appointment.setStatus(AppointmentStatus.OPEN);
        appointmentRepository.save(appointment);

        //when and then
        mockMvc.perform(delete("/patient/deleteAppointment/" + appointment.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        var updatedAppointment = appointmentRepository.findById(appointment.getId()).orElseThrow();
        Assertions.assertEquals(AppointmentStatus.CANCELLED, updatedAppointment.getStatus());
    }
}
