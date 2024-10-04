package com.med.viral.model;

import com.med.viral.model.security.Token;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@ToString
@Getter
@Setter
@RequiredArgsConstructor
@Entity
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
@SuperBuilder
@Table(name = "_user")
public abstract class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String firstname;
    private String lastname;
    @Column(unique = true)
    private String email;
    private Long pesel;
    @Column(unique = true)
    private String username;
    private String password;
    @ToString.Exclude
    @OneToMany(mappedBy = "user")
    private List<Token> tokens;
    private boolean accountNonLocked;

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }
}
