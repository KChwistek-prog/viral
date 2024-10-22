package com.med.viral.model;

import com.med.viral.model.security.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

@ToString
@Getter
@Setter
@RequiredArgsConstructor
@SuperBuilder
@Entity
@AllArgsConstructor
@DiscriminatorValue("DOCTOR")
public class Doctor extends User {

    private String specialization;

    @OneToMany
    @ToString.Exclude
    private List<Appointment> appointment;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

}