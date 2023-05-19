package com.oc.rapportmicroservice.service;

import com.oc.rapportmicroservice.exception.ResourceNotFoundException;
import com.oc.rapportmicroservice.model.Patient;
import com.oc.rapportmicroservice.service.implement.PatientServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceImplTest {
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PatientServiceImpl patientService;

    @Value("${API_PATIENTS_DOCKER_PAT_ID}")
    private String patientUrlDockerByPatId;

    @Value("${API_PATIENTS_DOCKER_PAT_NAME}")
    private String patientUrlDockerByPatName;
    @Test
    void testGetPatientByPatId() {
        // Given
        Long patientId = 5L;
        Patient patient = new Patient(patientId, "Doe", "John",
                LocalDate.of(1983, 10, 18), "M", "31 Box Street", "222-556-4123");
        when(restTemplate.getForObject(patientUrlDockerByPatId, Patient.class, patientId)).thenReturn(patient);

        // When
        Patient patientLoaded = patientService.getPatientByPatId(patientId);

        // Then
        assertThat(patientLoaded.dateOfBirth()).isEqualTo(patient.dateOfBirth());
        verify(restTemplate, times(1)).getForObject(patientUrlDockerByPatId, Patient.class, patientId);
    }

    @Test
    void testGetPatientByPatIdShouldThrowError() {
        // Given
        Long patientId = 5L;

        when(restTemplate.getForObject(patientUrlDockerByPatId, Patient.class, patientId)).thenReturn(null);

        // Then
        assertThatThrownBy(() -> patientService.getPatientByPatId(patientId))
                .isInstanceOf(ResourceNotFoundException.class)
                        .hasMessageContaining("No Patient found with this patient in the database.");

        verify(restTemplate, times(1)).getForObject(patientUrlDockerByPatId, Patient.class, patientId);
    }
    @Test
    void testGetPatientByPatLastName() {
        // Given
        Long patientId = 5L;
        Patient patient = new Patient(patientId, "Doe", "John",
                LocalDate.of(1983, 10, 18), "M", "31 Box Street", "222-556-4123");
        when(restTemplate.getForObject(patientUrlDockerByPatName, Patient.class, patient.lastName())).thenReturn(patient);

        // When
        Patient patientLoaded = patientService.getPatientByPatLastName(patient.lastName());

        // Then
        assertThat(patientLoaded.dateOfBirth()).isEqualTo(patient.dateOfBirth());
        verify(restTemplate, times(1)).getForObject(patientUrlDockerByPatName, Patient.class, patient.lastName());
    }

    @Test
    void testGetPatientByPatLastNameShouldThrowError() {
        // Given
        String lastName = "Doe";

        when(restTemplate.getForObject(patientUrlDockerByPatName, Patient.class, lastName)).thenReturn(null);

        // Then
        assertThatThrownBy(() -> patientService.getPatientByPatLastName(lastName))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No Patient found with this lastName: " + lastName + " in the database.");

        verify(restTemplate, times(1)).getForObject(patientUrlDockerByPatName, Patient.class, lastName);
    }
}