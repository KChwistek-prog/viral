package com.med.viral.model;

import com.med.viral.model.security.Role;
import com.med.viral.model.security.Token;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

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

    @Override
    public String
    toString() {
        return "Doctor{" +
                "id=" + id +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", pesel=" + pesel +
                ", email='" + email + '\'' +
                ", specialization='" + specialization + '\'' +
                ", role=" + role +
                ", isAccountNonLocked=" + isAccountNonLocked +
                '}';
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Doctor doctor = (Doctor) o;
        return getId() != null && Objects.equals(getId(), doctor.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
