package com.med.viral.service;

import com.med.viral.model.Patient;

import java.util.List;

public interface PatientService {
    Patient savePatient(Patient patient);

    List<Patient> fetchPatientList();

    Patient updatePatient(Patient patient, Integer id);

    void deletePatientById(Integer patientId);
}
