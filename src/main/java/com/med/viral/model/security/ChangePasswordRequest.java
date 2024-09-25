package com.med.viral.model.security;

public record ChangePasswordRequest(String currentPassword,
                                    String newPassword,
                                    String confirmationPassword) {
}
