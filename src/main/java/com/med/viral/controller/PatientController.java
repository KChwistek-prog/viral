package com.med.viral.controller;

import com.med.viral.exceptions.UserNotFoundException;
import com.med.viral.model.Appointment;
import com.med.viral.model.AppointmentStatus;
import com.med.viral.repository.AppointmentRepository;
import com.med.viral.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/patient")
@PreAuthorize("hasRole('PATIENT')")
public class PatientController {

    UserService service;
    AppointmentRepository appointmentRepository;

    public PatientController(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @PostMapping("/addAppointment/{id}")
    public ResponseEntity<Appointment> addAppointment(@PathVariable("id") Integer docId) throws UserNotFoundException {
        UserDetails loggedUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var user = service.getByUsername(loggedUser.getUsername());
        var doctor = service.getById(docId);
        Appointment newAppointment = null;
        if (user.isAccountNonLocked()) {
            Appointment.builder()
                    .patient_id(user.id())
                    .doctor_id(doctor.id())
                    .date(new Date())
                    .status(AppointmentStatus.OPEN)
                    .build();
            return ResponseEntity.ok(appointmentRepository.save(newAppointment));
        } else return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/deleteAppointment/{id}")
    public void cancelAppointment(@PathVariable("id") Long appointmentId) {
        var appointment = appointmentRepository.findById(appointmentId).orElseThrow();
        appointmentRepository.delete(appointment);
    }
}
