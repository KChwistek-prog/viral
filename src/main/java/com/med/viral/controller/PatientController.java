package com.med.viral.controller;

import com.med.viral.model.Appointment;
import com.med.viral.model.AppointmentStatus;
import com.med.viral.repository.AppointmentRepository;
import com.med.viral.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    UserRepository repository;
    AppointmentRepository appointmentRepository;

    @Autowired
    public PatientController(UserRepository repository, AppointmentRepository appointmentRepository) {
        this.repository = repository;
        this.appointmentRepository = appointmentRepository;
    }

    @PostMapping("/addAppointment/{id}")
    public ResponseEntity<Appointment> addAppointment(@PathVariable("id") Integer docId) {
        UserDetails loggedUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var user = repository.findByEmail(loggedUser.getUsername()).orElseThrow();
        var doctor = repository.findById(docId).orElseThrow();
        Appointment newAppointment = null;
        if (user.isAccountNonLocked()) {
            Appointment.builder()
                    .patient_id(user.getId())
                    .doctor_id(doctor.getId())
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
