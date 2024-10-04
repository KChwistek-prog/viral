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
@DiscriminatorValue("ADMIN")
public class Admin extends User {

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "admin")
    @ToString.Exclude
    private List<Action> actions;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.role.getAuthorities();
    }
}
