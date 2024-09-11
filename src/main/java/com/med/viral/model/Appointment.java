package com.med.viral.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.util.Date;
@Data
@Entity
public class Appointment {
    @Id
    private  Long id;
    @ManyToOne
    private  Patient patient;
    @ManyToOne
    private  Doctor doctor;
    private  Date date;
}
