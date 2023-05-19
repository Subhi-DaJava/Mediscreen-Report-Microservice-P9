package com.oc.rapportmicroservice.service.implement;

import com.oc.rapportmicroservice.exception.ResourceNotFoundException;
import com.oc.rapportmicroservice.model.Patient;
import com.oc.rapportmicroservice.service.PatientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
@Service
public class PatientServiceImpl implements PatientService {
    private static final Logger logger = LoggerFactory.getLogger(PatientServiceImpl.class);

    private final RestTemplate restTemplate;

    @Value("${API_PATIENTS_DOCKER_PAT_ID}")
    private String patientUrlDockerByPatId;

    @Value("${API_PATIENTS_DOCKER_PAT_NAME}")
    private String patientUrlDockerByPatName;


    public PatientServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Patient getPatientByPatId(Long patientId) {
        logger.debug("getPatientById method starts here, from PatientServiceImpl");

        Patient patient = restTemplate.getForObject(patientUrlDockerByPatId, Patient.class, patientId);

        if (patient == null) {
            logger.error("No Patient found with this PatientId: {}", patientId);
            throw new ResourceNotFoundException("No Patient found with this patient in the database.");
        }

        logger.info("Patient with ID {} has been successfully retrieved, from getPatientById method, in PatientServiceImpl", patientId);

        return patient;
    }

    @Override
    public Patient getPatientByPatLastName(String lastName) {
        logger.debug("getPatientByLastName method starts here, from PatientServiceImpl");

        Patient patient = restTemplate.getForObject(patientUrlDockerByPatName, Patient.class, lastName);

        if (patient == null) {
            logger.error("No Patient found with this LastName: {}", lastName);
            throw new ResourceNotFoundException("No Patient found with this lastName: " + lastName + " in the database.");
        }

        logger.info("Patient with LastName {} has been successfully retrieved, from getPatientByLastName method, in PatientServiceImpl", lastName);

        return patient;
    }
}
