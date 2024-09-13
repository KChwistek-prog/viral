package com.med.viral.model;

import lombok.Getter;

@Getter
public enum RoleType {
    PATIENT("ROLE_PATIENT"),
    DOCTOR("ROLE_DOCTOR"),
    ADMIN("ROLE_ADMIN");
    private String value;

    RoleType(String value) {
        this.value = value;
    }

}
