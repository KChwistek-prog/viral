package com.med.viral.controller;

import com.med.viral.model.Appointment;
import com.med.viral.model.DTO.UserDTO;
import com.med.viral.model.User;
import com.med.viral.repository.AppointmentRepository;
import com.med.viral.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/doctor")
@PreAuthorize("hasRole('DOCTOR')")
public class DoctorController {

    UserService userService;
    AppointmentRepository appointmentRepository;

    public DoctorController(UserService userService, AppointmentRepository appointmentRepository) {
        this.userService = userService;
        this.appointmentRepository = appointmentRepository;
    }

    @GetMapping("/getPatients")
    public ResponseEntity<List<UserDTO>> getPatientList() {
        User loggedDoc = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var appointments = appointmentRepository.findAll();
        var patients = appointments.stream()
                .filter(a -> a.getDoctor_id().equals(loggedDoc.getId()))
                .map(Appointment::getPatient_id)
                .collect(Collectors.toSet());
        return ResponseEntity.ok(userService.getAllPatientsByIds(patients));
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
