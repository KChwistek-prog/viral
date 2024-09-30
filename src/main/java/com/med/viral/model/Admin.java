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
public class Admin implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String firstname;
    private String lastname;
    @Column(unique = true)
    private String username;
    private String password;
    @Column(unique = true)
    private String email;
    private Integer pesel;
    @Enumerated(EnumType.STRING)
    private Role role;
    @OneToMany(mappedBy = "admin")
    private List<Token> tokens;
    @OneToMany(mappedBy = "admin")
    private List<Action> actions;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.role.getAuthorities();
    }

    @Override
    public String toString() {
        return "Admin{" +
                "id=" + id +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", pesel=" + pesel +
                ", role=" + role +
                '}';
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Admin admin = (Admin) o;
        return getId() != null && Objects.equals(getId(), admin.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }


}
