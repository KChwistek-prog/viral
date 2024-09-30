package com.med.viral.model.DTO;

import com.med.viral.model.Action;
import com.med.viral.model.security.Role;
import com.med.viral.model.security.Token;

import java.util.List;

public record AdminDTO(Integer id,
                       String firstname,
                       String lastname,
                       String username,
                       String password,
                       String email,
                       Integer pesel,
                       Role role,
                       List<Token> tokens,
                       List<Action> actions) {
}
