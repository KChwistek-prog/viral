package com.med.viral.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Admin {
    @Id
    private Integer id;
    private String name;
    private String surname;
    private Integer pesel;
    private String login;
    private String password;
    @OneToMany
    private List<Action> actions;
}
