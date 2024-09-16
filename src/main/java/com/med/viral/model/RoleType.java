package com.med.viral.model;

import lombok.Getter;

@Getter
public enum RoleType {
    ADMIN("ROLE_ADMIN"),
    DOCTOR("ROLE_DOCTOR"),
    PATIENT("ROLE_PATIENT");

    private final String value;

    RoleType(String value) {
        this.value = value;
    }

}
