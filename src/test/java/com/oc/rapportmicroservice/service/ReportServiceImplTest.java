package com.oc.rapportmicroservice.service;

import com.oc.rapportmicroservice.exception.ResourceNotFoundException;
import com.oc.rapportmicroservice.model.Note;
import com.oc.rapportmicroservice.model.Patient;
import com.oc.rapportmicroservice.model.Report;
import com.oc.rapportmicroservice.service.implement.ReportServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private ReportServiceImpl reportService;

    @Test
    void testGetReportByPatIdShouldReturnReport() {
        Long patientId = 5L;
       //String patientUrl = "http://patient-service/api/patients/{id}";
        String patientUrl = "http://localhost:8081/api/patients/{id}";
        //String noteUrl = "http://note-service/api/notes/by-patId/{id}";
       String noteUrl = "http://localhost:8082/api/notes/by-patId/{patId}";

        Patient patient = new Patient(patientId, "Doe", "John",
                LocalDate.of(1983, 10, 18), "M", "31 Box Street", "222-556-4123");

        when(restTemplate.getForObject(patientUrl, Patient.class, patientId)).thenReturn(patient);

        Note[] notes = {
                new Note(5L, "Doe", "Comment 1", LocalDate.now()),
                new Note(5L, "Doe", "Comment 2", LocalDate.now())
        };

        when(restTemplate.getForObject(noteUrl, Note[].class, patientId)).thenReturn(notes);


        // When
        Report report = reportService.getReportByPatId(patientId);

        // Then
        assertThat(report.getPatId()).isEqualTo(patientId);
        assertThat(report.getPatFullName()).isEqualTo("John Doe");

        verify(restTemplate, times(1)).getForObject(patientUrl, Patient.class, patientId);
        verify(restTemplate, times(1)).getForObject(noteUrl, Note[].class, patientId);

    }

    @Test
    void testGetReportByPatIdShouldThrowResourceNotFoundException() {
        // Given
        Long patientId = 5L;
        //String patientUrl = "http://patient-service/api/patients/{id}";
        String patientUrl = "http://localhost:8081/api/patients/{id}";

        when(restTemplate.getForObject(patientUrl, Patient.class, patientId)).thenReturn(null);

        // Then
        assertThatThrownBy(() -> reportService.getReportByPatId(patientId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No Patient found with this patient id:{%d} in the database.".formatted(patientId));

    }
    @Test
    void testGetReportByPatLastName() {
        Long patientId = 5L;
        //String patientUrl = "http://patient-microservice:8081/api/patient?lastName={lastName}";
        String patientUrl = "http://localhost:8081/api/patient?lastName={lastName}";
        //String noteUrl = "http://note-microservice:8082/api/notes/by-lastName/{lastName}";
        String noteUrl = "http://localhost:8082/api/notes/by-lastName/{lastName}";

        Patient patient = new Patient(patientId, "Doe", "John",
                LocalDate.of(1983, 10, 18), "M", "31 Box Street", "222-556-4123");

        when(restTemplate.getForObject(patientUrl, Patient.class, patient.lastName())).thenReturn(patient);

        Note[] notes = {
                new Note(5L, "Doe", "Comment 1", LocalDate.now()),
                new Note(5L, "Doe", "Comment 2", LocalDate.now())
        };

        when(restTemplate.getForObject(noteUrl, Note[].class, patient.lastName())).thenReturn(notes);


        // When
        Report report = reportService.getReportByPatLastName(patient.lastName());

        // Then
        assertThat(report.getPatId()).isEqualTo(patientId);
        assertThat(report.getPatFullName()).isEqualTo("John Doe");

        verify(restTemplate, times(1)).getForObject(patientUrl, Patient.class, patient.lastName());
        verify(restTemplate, times(1)).getForObject(noteUrl, Note[].class, patient.lastName());
    }
    @Test
    void testGetReportByPatLastNameShouldThrowResourceNotFoundException() {
        // Given
        String patientLastName = "lastName";
        //String patientUrl = "http://patient-microservice:8081/api/patient?lastName={lastName}";
        String patientUrl = "http://localhost:8081/api/patient?lastName={lastName}";

        when(restTemplate.getForObject(patientUrl, Patient.class, patientLastName)).thenReturn(null);

        // Then
        assertThatThrownBy(() -> reportService.getReportByPatLastName("lastName"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No Patient found with this patient id:{%s} in the database.".formatted(patientLastName));

    }
    @Test
    void testCountTriggerWordsShouldReturnZero() {
        // Given
        List<String> comments = new ArrayList<>(List.of("None", "Borderline", "In Danger", "Early onset"));

        // When
        long totalNumberOfTriggers = reportService.countTriggerWords(comments);

        // Then
        assertThat(totalNumberOfTriggers).isEqualTo(0);
    }

    @Test
    void testCountTriggerWordsShouldReturnTwo() {
        // Given
        List<String> comments = new ArrayList<>(List.of("None", "Borderline", "In Danger", "Early onset", "Poids", "Taille", "poids"));

        // When
        long totalNumberOfTriggers = reportService.countTriggerWords(comments);

        // Then
        assertThat(totalNumberOfTriggers).isEqualTo(2);
    }
    @Test
    void getCalculateRiskLevelWhenNumTriggerEqualsToZero() {
        // Given
        long age = 35;
        String gender = "M";
        long numTriggers = 0;

        // When
        String riskLevel = reportService.calculateRiskLevel(age, gender, numTriggers);

        // Then
        assertThat(riskLevel).isEqualTo("None");

    }

    @Test
    void getCalculateRiskLevelWhenNumTriggerEqualsToTwoAndAgeMoreThen30() {
        // Given
        long age = 35;
        String gender = "M";
        long numTriggers = 2;

        // When
        String riskLevel = reportService.calculateRiskLevel(age, gender, numTriggers);

        // Then
        assertThat(riskLevel).isEqualTo("Borderline");

    }

    @Test
    void getCalculateRiskLevelWhenNumTriggerEqualsToTwoAndAgeLessThen30() {
        // Given
        long age = 29;
        String gender = "M";
        long numTriggers = 2;

        // When
        String riskLevel = reportService.calculateRiskLevel(age, gender, numTriggers);

        // Then
        assertThat(riskLevel).isEqualTo("Unknown");

    }

    @Test
    void getCalculateRiskLevelWhenNumTriggerEqualsToThreeAndAgeLessThen30() {
        // Given
        long age = 29;
        String gender = "M";
        long numTriggers = 3;

        // When
        String riskLevel = reportService.calculateRiskLevel(age, gender, numTriggers);

        // Then
        assertThat(riskLevel).isEqualTo("In Danger");

    }
}