package com.med.viral.controller;

import com.med.viral.exceptions.AppointmentNotFoundException;
import com.med.viral.model.Appointment;
import com.med.viral.model.DTO.PatientDTO;
import com.med.viral.model.Doctor;
import com.med.viral.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/doctor")
@PreAuthorize("hasRole('DOCTOR')")
@RequiredArgsConstructor
public class DoctorController {

    private final AppointmentService appointmentService;

    @GetMapping("/getPatients")
    public ResponseEntity<Set<PatientDTO>> getPatientList() {
        var loggedDoc = (Doctor) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(appointmentService.getAllPatients(loggedDoc.getId()));
    }

    @DeleteMapping("/deleteAppointment/{id}")
    public ResponseEntity<Void> cancelAppointment(@PathVariable("id") Long id) throws AppointmentNotFoundException {
        var appointment = appointmentService.getAppointmentById(id);
        appointmentService.cancelAppointment(appointment);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/getAppointments")
    public ResponseEntity<List<Appointment>> getPatientsAppointments() {
        var loggedDoc = (Doctor) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(appointmentService.getAppointmentsByDoctor(loggedDoc.getId()));
    }

}
