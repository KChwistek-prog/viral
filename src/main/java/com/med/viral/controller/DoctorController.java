package com.med.viral.controller;

import com.med.viral.model.Appointment;
import com.med.viral.model.User;
import com.med.viral.repository.AppointmentRepository;
import com.med.viral.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/doctor")
@PreAuthorize("hasRole('DOCTOR')")
public class DoctorController {

    UserRepository userRepository;
    AppointmentRepository appointmentRepository;

    @Autowired
    public DoctorController(UserRepository userRepository, AppointmentRepository appointmentRepository) {
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @GetMapping("/getPatients")
    public ResponseEntity<Set<User>> getPatientList() {
        User loggedDoc = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(appointmentRepository.findAll().stream()
                .filter(a -> a.getDoctor_id().equals(loggedDoc.getId()))
                .map(a -> userRepository.findById(a.getPatient_id()))
                .map(Optional::orElseThrow)
                .collect(Collectors.toSet()));
    }

    @DeleteMapping("/deleteAppointment/{id}")
    public void cancelAppointment(@PathVariable("id") Long id) {
        appointmentRepository.delete(appointmentRepository.findById(id).orElseThrow());
    }

    @GetMapping("/getAppointments")
    public ResponseEntity<List<Appointment>> getPatientsAppointments() {
        User loggedDoc = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(appointmentRepository.findAll().stream()
                .filter(a -> a.getDoctor_id().equals(loggedDoc.getId()))
                .toList());
    }

}
