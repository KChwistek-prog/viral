package com.med.viral.model.security;

import lombok.Builder;

@Builder
public record ChangePasswordRequest(String currentPassword,
                                    String newPassword,
                                    String confirmationPassword) {
}
