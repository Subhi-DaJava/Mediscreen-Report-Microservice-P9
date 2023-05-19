package com.oc.rapportmicroservice.service;

import com.oc.rapportmicroservice.model.Patient;

public interface PatientService {
    Patient getPatientByPatId(Long patientId);
    Patient getPatientByPatLastName(String patientName);
}
