package com.med.viral.model.DTO;

import com.med.viral.model.security.Role;

public record CreateUserDTO(String firstname,
                            String lastname,
                            String username,
                            String password,
                            Role role) {
}
