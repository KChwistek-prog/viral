package com.med.viral.model;

import com.med.viral.model.security.Role;
import com.med.viral.model.security.Token;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@Builder
@Entity
@AllArgsConstructor
public class Doctor implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String firstname;
    private String lastname;
    @Column(unique = true)
    private String username;
    private String password;
    private Integer pesel;
    private String email;
    private String specialization;
    @OneToMany
    private List<Appointment> appointment;
    @Enumerated(EnumType.STRING)
    private Role role;
    @OneToMany(mappedBy = "doctor")
    private List<Token> tokens;
    private boolean isAccountNonLocked;

    @Override
    public boolean isAccountNonLocked() {
        return this.isAccountNonLocked;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }
}
