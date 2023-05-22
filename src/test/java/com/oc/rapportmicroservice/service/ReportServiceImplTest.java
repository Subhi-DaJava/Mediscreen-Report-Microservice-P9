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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {
    @Mock
    private PatientService patientService;
    @Mock
    private NoteService noteService;

    @InjectMocks
    private ReportServiceImpl reportService;
    @Test
    void testGetReportByPatIdShouldReturnReport() {
        // Given
        Long patientId = 5L;
        Patient patient = new Patient(patientId, "Doe", "John",
                LocalDate.of(1983, 10, 18), "M", "31 Box Street", "222-556-4123");

        when(patientService.getPatientByPatId(patientId)).thenReturn(patient);

        Note[] notes = {
                new Note(5L, "Doe", "Comment 1", LocalDate.now()),
                new Note(5L, "Doe", "Comment 2", LocalDate.now())
        };

        when(noteService.getNotesByPatId(patientId)).thenReturn(notes);

        // When
        Report report = reportService.getReportByPatId(patientId);

        // Then
        assertThat(report.getPatId()).isEqualTo(5);

        verify(patientService, times(1)).getPatientByPatId(anyLong());
        verify(noteService, times(1)).getNotesByPatId(anyLong());

    }

    @Test
    void testGetReportByPatIdShouldThrowNoPatientError() {
        // Given
        Long patientId = 5L;

        when(patientService.getPatientByPatId(patientId)).thenReturn(null);

        // Then
        assertThatThrownBy(()-> reportService.getReportByPatId(patientId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No Patient found in the database.");

        verify(patientService, times(1)).getPatientByPatId(anyLong());

    }

    @Test
    void testGetReportByPatIdShouldThrowError() {
        // Given
        Long patientId = 5L;
        Patient patient = new Patient(patientId, "Doe", "John",
                LocalDate.of(1983, 10, 18), "M", "31 Box Street", "222-556-4123");

        when(patientService.getPatientByPatId(patientId)).thenReturn(patient);

        Note[] notes = {};

        when(noteService.getNotesByPatId(patientId)).thenReturn(notes);

        // then

        assertThatThrownBy(()-> reportService.getReportByPatId(patientId))
                .isInstanceOf(ResourceNotFoundException.class)
                        .hasMessageContaining("No notes found for patient id: " + patientId + " in the database.");

        verify(patientService, times(1)).getPatientByPatId(anyLong());
        verify(noteService, times(1)).getNotesByPatId(anyLong());

    }

    @Test
    void testGetReportByPatLastNameShouldReturnReport() {
        // Given
        Long patientId = 5L;
        Patient patient = new Patient(patientId, "Doe", "John",
                LocalDate.of(1983, 10, 18), "M", "31 Box Street", "222-556-4123");

        when(patientService.getPatientByPatLastName("Doe")).thenReturn(patient);

        Note[] notes = {
                new Note(5L, "Doe", "Comment 1", LocalDate.now()),
                new Note(5L, "Doe", "Comment 2", LocalDate.now())
        };

        when(noteService.getNotesByPatLastName("Doe")).thenReturn(notes);

        // When
        Report report = reportService.getReportByPatLastName("Doe");

        // Then
        assertThat(report.getPatId()).isEqualTo(patientId);
        assertThat(report.getPatFullName()).isEqualTo("John Doe");

        verify(patientService, times(1)).getPatientByPatLastName(anyString());
        verify(noteService, times(1)).getNotesByPatLastName(anyString());

    }

    @Test
    void testGetReportByPatLastNameShouldThrowNoPatientError() {
        // Given
       String lastName = "lastName";

        when(patientService.getPatientByPatLastName(lastName)).thenReturn(null);

        // Then
        assertThatThrownBy(()-> reportService.getReportByPatLastName(lastName))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No Patient found in the database.");

        verify(patientService, times(1)).getPatientByPatLastName(lastName);

    }
    @Test
    void testGetReportByPatLastNameShouldThrowError() {
        // Given
        Long patientId = 5L;
        Patient patient = new Patient(patientId, "Doe", "John",
                LocalDate.of(1983, 10, 18), "M", "31 Box Street", "222-556-4123");

        when(patientService.getPatientByPatLastName("Doe")).thenReturn(patient);

        Note[] notes = {};

        when(noteService.getNotesByPatLastName("Doe")).thenReturn(notes);

        // Then

        assertThatThrownBy(() -> reportService.getReportByPatLastName("Doe"))
                .isInstanceOf(ResourceNotFoundException.class)
                        .hasMessageContaining("No notes found for patient lastName: " + patient.lastName() + " in the database.");

        verify(patientService, times(1)).getPatientByPatLastName(anyString());
        verify(noteService, times(1)).getNotesByPatLastName(anyString());

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
    void testGetCalculateRiskLevelWhenNumTriggerEqualsToZero() {
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
    void testGetCalculateRiskLevelWhenNumTriggerEqualsToTwoAndAgeMoreThen30() {
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
    void testGetCalculateRiskLevelWhenNumTriggerEqualsToTwoAndAgeLessThen30() {
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
    void testGtCalculateRiskLevelWhenNumTriggerEqualsToThreeAndAgeLessThen30() {
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