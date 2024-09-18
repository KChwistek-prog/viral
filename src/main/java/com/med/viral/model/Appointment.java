package com.med.viral.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
@Data
@Entity
@Builder
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private  Long id;
    private  Integer patient_id;
    private  Integer doctor_id;
    private  Date date;
    private  AppointmentStatus status;
}
