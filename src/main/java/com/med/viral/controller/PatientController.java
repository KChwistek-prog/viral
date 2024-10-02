package com.med.viral.controller;

import com.med.viral.exceptions.AppointmentNotFoundException;
import com.med.viral.exceptions.UserNotFoundException;
import com.med.viral.model.Appointment;
import com.med.viral.model.User;
import com.med.viral.service.AppointmentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patient")
@PreAuthorize("hasRole('PATIENT')")
@AllArgsConstructor
public class PatientController {
    private final AppointmentService appointmentService;

    @PostMapping("/addAppointment/{id}")
    public ResponseEntity<Appointment> addAppointment(@PathVariable("id") Integer docId) throws UserNotFoundException {
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(appointmentService.registerAppointment(loggedUser, docId));
    }

    @DeleteMapping("/deleteAppointment/{id}")
    public void cancelAppointment(@PathVariable("id") Long appointmentId) throws AppointmentNotFoundException {
        appointmentService.cancelAppointment(appointmentId);
    }
}
