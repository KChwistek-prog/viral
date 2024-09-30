package com.med.viral.model.DTO;

import com.med.viral.model.Appointment;
import com.med.viral.model.security.Role;

import java.util.List;

public record DoctorDTO(Integer id,
                        String firstname,
                        String lastname,
                        String username,
                        String password,
                        Integer pesel,
                        String email,
                        String specialization,
                        List<Appointment> appointment,
                        Role role) {
}
