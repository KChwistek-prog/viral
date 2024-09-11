package com.med.viral.service;

import com.med.viral.model.Doctor;

import java.util.List;

public interface DoctorService {
    Doctor saveDoctor(Doctor doctor);

    List<Doctor> fetchDoctorList();

    Doctor updateDoctor(Doctor doctor, Integer id);

    void deleteDoctorById(Integer doctorId);
}
