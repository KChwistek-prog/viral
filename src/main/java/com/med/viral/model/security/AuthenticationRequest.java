package com.med.viral.model.security;

public record AuthenticationRequest(String username,
                                    String password,
                                    Role role) {
}
