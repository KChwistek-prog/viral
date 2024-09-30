package com.med.viral.service;

import com.med.viral.exceptions.AppointmentNotFoundException;
import com.med.viral.model.AppointmentStatus;
import com.med.viral.repository.AppointmentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;


    public void cancelAppointment(Long appointmentId) throws AppointmentNotFoundException {
        var appointment = appointmentRepository.findById(appointmentId);
        appointment
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment Not Found"))
                .setStatus(AppointmentStatus.CANCELLED);
    }
}
