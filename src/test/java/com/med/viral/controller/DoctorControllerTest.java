package com.med.viral.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.med.viral.model.*;
import com.med.viral.model.security.AuthenticationRequest;
import com.med.viral.model.security.AuthenticationResponse;
import com.med.viral.model.security.Role;
import com.med.viral.repository.AppointmentRepository;
import com.med.viral.repository.DoctorRepository;
import com.med.viral.repository.PatientRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DoctorControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    AppointmentRepository appointmentRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        var doctor = new Doctor();
        doctor.setFirstname("John");
        doctor.setLastname("Smith");
        doctor.setUsername("doctor2");
        doctor.setPassword(passwordEncoder.encode("1234"));
        doctor.setRole(Role.DOCTOR);
        doctor.setAccountNonLocked(true);
        doctorRepository.save(doctor);

        var patient = new Patient();
        patient.setFirstname("John");
        patient.setLastname("Doe");
        patient.setUsername("patient2");
        patient.setPassword(passwordEncoder.encode("1234"));
        patient.setRole(Role.PATIENT);
        patient.setAccountNonLocked(true);
        patientRepository.save(patient);

        Appointment appointment = new Appointment();
        appointment.setDoctor(doctorRepository.findByUsername("doctor2").get());
        appointment.setPatient(patientRepository.findByUsername("patient2").get());
        appointment.setDate(new Date());
        appointment.setStatus(AppointmentStatus.OPEN);
        appointmentRepository.save(appointment);
    }

    @Test
    void getPatientList() throws Exception {
        var loginRequest = new AuthenticationRequest("doctor2", "1234", Role.DOCTOR);
        var response = mockMvc.perform(post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn().getResponse().getContentAsString();
        var token = objectMapper.readValue(response, AuthenticationResponse.class).accessToken();

        mockMvc.perform(get("/doctor/getPatients")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void cancelAppointment() throws Exception {
        Appointment appointment = new Appointment();
        appointment.setDoctor(doctorRepository.findByUsername("doctor2").get());
        appointment.setPatient(patientRepository.findByUsername("patient2").get());
        appointment.setDate(new Date());
        appointment.setStatus(AppointmentStatus.OPEN);
        appointmentRepository.save(appointment);

        var loginRequest = new AuthenticationRequest("doctor2", "1234", Role.DOCTOR);
        var response = mockMvc.perform(post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn().getResponse().getContentAsString();
        var token = objectMapper.readValue(response, AuthenticationResponse.class).accessToken();

        mockMvc.perform(delete("/doctor/deleteAppointment/" + appointment.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    void getPatientsAppointments() throws Exception {
        var loginRequest = new AuthenticationRequest("doctor2", "1234", Role.DOCTOR);
        var response = mockMvc.perform(post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn().getResponse().getContentAsString();
        var token = objectMapper.readValue(response, AuthenticationResponse.class).accessToken();

        mockMvc.perform(get("/doctor/getAppointments")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

}
