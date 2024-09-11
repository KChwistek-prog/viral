package com.med.viral.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.List;
@Data
@Entity
public class Doctor {

    @Id
    private Integer id;
    private String name;
    private String surname;
    private Integer pesel;
    private String specialization;
    private Short age;
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    private List<Appointment> appointment;
}
