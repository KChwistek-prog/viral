package com.med.viral.model.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.med.viral.model.security.Permission.*;

@RequiredArgsConstructor
public enum Role {

    USER(Collections.emptySet()),
    ADMIN(
            Set.of(ADMIN_READ,
                    ADMIN_UPDATE,
                    ADMIN_DELETE,
                    ADMIN_CREATE,
                    DOCTOR_READ,
                    DOCTOR_UPDATE,
                    DOCTOR_DELETE,
                    DOCTOR_CREATE)),
    DOCTOR(
            Set.of(DOCTOR_READ,
                    DOCTOR_UPDATE,
                    DOCTOR_DELETE,
                    DOCTOR_CREATE)),
    PATIENT(
            Set.of(PATIENT_READ,
                    PATIENT_UPDATE,
                    PATIENT_DELETE,
                    PATIENT_CREATE));

    private final Set<Permission> permissions;

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(getPermissions().toString()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}