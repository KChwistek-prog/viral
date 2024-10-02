package com.med.viral.controller;

import com.med.viral.model.Appointment;
import com.med.viral.model.DTO.UserDTO;
import com.med.viral.model.Doctor;
import com.med.viral.repository.AppointmentRepository;
import com.med.viral.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/doctor")
@PreAuthorize("hasRole('DOCTOR')")
@AllArgsConstructor
public class DoctorController {

    UserService userService;
    AppointmentRepository appointmentRepository;

    @GetMapping("/getPatients")
    public ResponseEntity<Set<UserDTO>> getPatientList() {
        var loggedDoc = (Doctor) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(userService.getAllPatients(loggedDoc.getId()));
    }

    @DeleteMapping("/deleteAppointment/{id}")
    public void cancelAppointment(@PathVariable("id") Long id) {
        appointmentRepository.delete(appointmentRepository.findById(id).orElseThrow());
    }

    @GetMapping("/getAppointments")
    public ResponseEntity<List<Appointment>> getPatientsAppointments() {
        var loggedDoc = (Doctor) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(appointmentRepository.findAll().stream()
                .filter(a -> a.getDoctor().getId().equals(loggedDoc.getId()))
                .toList());
    }

}
