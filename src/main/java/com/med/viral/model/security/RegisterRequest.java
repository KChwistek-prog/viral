package com.med.viral.model.security;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record RegisterRequest(String firstname,
                              String lastname,
                              @NotNull(message = "Username is required") String username,
                              @NotNull(message = "Password is required") String password,
                              @NotNull(message = "Role is required") Role role) {
}