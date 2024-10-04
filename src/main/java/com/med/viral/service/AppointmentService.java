package com.med.viral.service;

import com.med.viral.exceptions.AppointmentNotFoundException;
import com.med.viral.exceptions.UserNotFoundException;
import com.med.viral.model.Appointment;
import com.med.viral.model.DTO.PatientDTO;
import com.med.viral.model.Patient;

import java.util.List;
import java.util.Set;

public interface AppointmentService {
    void cancelAppointment(Appointment appointment) throws AppointmentNotFoundException;

    Appointment registerAppointment(Patient patient, Integer doctorId) throws UserNotFoundException;

    List<Appointment> getAppointmentsByDoctor(Integer id);

    Set<PatientDTO> getAllPatients(Integer doctorId);

    Appointment getAppointmentById(Long appointmentId) throws AppointmentNotFoundException;

    void changeAppointmentStatus(Appointment appointment, String status) throws AppointmentNotFoundException;
}
