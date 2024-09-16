package com.med.viral.model.security;

import lombok.Builder;

@Builder
public record AuthenticationRequest(String email,
                                    String password) {

}
