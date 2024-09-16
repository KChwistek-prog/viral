package com.med.viral.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Date;
@Data
@Entity
public class Appointment {
    @Id
    private  Long id;
    private  User patient;
    private  User doctor;
    private  Date date;
}
