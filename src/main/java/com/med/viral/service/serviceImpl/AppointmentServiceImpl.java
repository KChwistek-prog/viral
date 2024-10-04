package com.med.viral.service.serviceImpl;

import com.med.viral.exceptions.AppointmentNotFoundException;
import com.med.viral.exceptions.UserNotFoundException;
import com.med.viral.model.Appointment;
import com.med.viral.model.AppointmentStatus;
import com.med.viral.model.DTO.PatientDTO;
import com.med.viral.model.Doctor;
import com.med.viral.model.Patient;
import com.med.viral.model.mapper.UserMapper;
import com.med.viral.repository.AppointmentRepository;
import com.med.viral.repository.DoctorRepository;
import com.med.viral.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final UserMapper userMapper;

    @Override
    public void cancelAppointment(Appointment appointment) throws AppointmentNotFoundException {
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    @Override
    public Appointment registerAppointment(Patient patient, Integer doctorId) throws UserNotFoundException {
        if (!patient.isAccountNonLocked()) {
            throw new UserNotFoundException("User Locked");
        }
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new UserNotFoundException("Doctor not found"));
        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .date(new Date())
                .status(AppointmentStatus.OPEN)
                .build();
        return appointmentRepository.save(appointment);
    }

    @Override
    public List<Appointment> getAppointmentsByDoctor(Integer id) {
        return appointmentRepository.findAll().stream()
                .filter(a -> a.getDoctor().getId().equals(id))
                .toList();
    }

    @Override
    public Set<PatientDTO> getAllPatients(Integer doctorId) {
        return appointmentRepository.findAll().stream()
                .filter(a -> a.getDoctor().getId().equals(doctorId))
                .map(Appointment::getPatient)
                .map(userMapper::PatientEntityToDTO)
                .collect(Collectors.toSet());
    }

    @Override
    public Appointment getAppointmentById(Long appointmentId) throws AppointmentNotFoundException {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment with id " + appointmentId + " does not exist"));
    }

    @Override
    public void changeAppointmentStatus(Appointment appointment, String status) throws AppointmentNotFoundException {
        if (status.equals(AppointmentStatus.OPEN.getValue())) {
            appointment.setStatus(AppointmentStatus.OPEN);
        } else if (status.equals(AppointmentStatus.CLOSED.getValue())) {
            appointment.setStatus(AppointmentStatus.CLOSED);
        } else if (status.equals(AppointmentStatus.CANCELLED.getValue())) {
            appointment.setStatus(AppointmentStatus.CANCELLED);
        } else throw new AppointmentNotFoundException("Status " + status + " does not exist");
        appointmentRepository.save(appointment);
    }
}
