package com.med.viral.model.DTO;

import com.med.viral.model.Appointment;
import com.med.viral.model.security.Role;
import com.med.viral.model.security.Token;

import java.util.List;

public record UserDTO(Integer id,
                      String firstname,
                      String lastname,
                      String username,
                      String password,
                      String email,
                      Integer pesel,
                      Short age,
                      String specialization,
                      List<Appointment> appointment,
                      Role role,
                      List<Token> tokens,
                      boolean isAccountNonLocked) {
}
