package com.med.viral.service;

import com.med.viral.exceptions.AppointmentNotFoundException;
import com.med.viral.exceptions.UserNotFoundException;
import com.med.viral.model.Appointment;
import com.med.viral.model.AppointmentStatus;
import com.med.viral.model.Doctor;
import com.med.viral.model.User;
import com.med.viral.repository.AppointmentRepository;
import com.med.viral.repository.DoctorRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@AllArgsConstructor
@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;

    public void cancelAppointment(Long appointmentId) throws AppointmentNotFoundException {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment Not Found"));
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    public Appointment registerAppointment(User user, Integer doctorId) throws UserNotFoundException {
        if (!user.isAccountNonLocked()) {
            throw new UserNotFoundException("User Locked");
        }
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new UserNotFoundException("Doctor not found"));
        Appointment appointment = Appointment.builder()
                .user(user)
                .doctor(doctor)
                .date(new Date())
                .status(AppointmentStatus.OPEN)
                .build();
        return appointmentRepository.save(appointment);
    }
}
