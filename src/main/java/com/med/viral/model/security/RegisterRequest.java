package com.med.viral.model.security;

import lombok.Builder;

@Builder
public record RegisterRequest(String firstname,
                              String lastname,
                              String username,
                              String password,
                              Role role) {
}