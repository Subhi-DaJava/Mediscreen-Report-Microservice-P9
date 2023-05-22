package com.oc.rapportmicroservice.integration_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oc.rapportmicroservice.controller.ReportController;
import com.oc.rapportmicroservice.model.Note;
import com.oc.rapportmicroservice.model.Patient;
import com.oc.rapportmicroservice.model.Report;
import com.oc.rapportmicroservice.service.NoteService;
import com.oc.rapportmicroservice.service.PatientService;
import com.oc.rapportmicroservice.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReportControllerIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ReportService reportService;

    @MockBean
    private PatientService patientService;
    @MockBean
    private NoteService noteService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetReportByPatIdShouldGenerateReport() throws Exception {
        // Given
        Long patId = 2L;
//        Report report = new Report(patId, "Lucas Ferguson", "Age: 35, Gender: M, Risk level: Borderline");
        Patient patient = new Patient(patId, "Doe", "John",
                LocalDate.of(1983, 10, 18), "M", "31 Box Street", "222-556-4123");
        Note[] notes = {
                new Note(2L, "Doe", "Poids, Taille", LocalDate.now()),
                new Note(2L, "Doe", "Fumeur", LocalDate.now())
        };

        when(patientService.getPatientByPatId(patId)).thenReturn(patient);
        when(noteService.getNotesByPatId(patId)).thenReturn(notes);

        // When
        mockMvc.perform(post("/assess/id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("patId", String.valueOf(patId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diabetesAssessment", is("Age: 39, Gender: M, Risk level: Borderline")))
                .andReturn();

    }

    @Test
    void testGetReportByPatIdShouldThrowError() throws Exception {
        // Given
        Long patId = 2L;
        Note[] notes = {};
        when(noteService.getNotesByPatId(patId)).thenReturn(notes);

        // When
        mockMvc.perform(post("/assess/id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("patId", String.valueOf(patId)))
                .andExpect(status().isNotFound())
                .andReturn();

    }
    @Test
    void testGetReportByPatIdShouldThrowNoPatientError() throws Exception {
        // Given
        Long patId = 2L;

        when(patientService.getPatientByPatId(patId)).thenReturn(null);

        // When
        mockMvc.perform(post("/assess/id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("patId", String.valueOf(patId)))
                .andExpect(status().isNotFound())
                .andReturn();

    }


    @Test
    void testGetReportByPatLastNameShouldGenerateReport() throws Exception {
        // Given
        String lastName = "Doe";
        //Report report = new Report(3L, "John Doe", "Age: 20, Gender: F, Risk level: In Danger");
        Patient patient = new Patient(3L, "Doe", "John",
                LocalDate.of(2003, 10, 18), "F", "31 Box Street", "222-556-4123");
        Note[] notes = {
                new Note(3L, "Doe", "Poids, Taille", LocalDate.now()),
                new Note(3L, "Doe", "Fumeur, Réaction", LocalDate.now())
        };

        when(patientService.getPatientByPatLastName(lastName)).thenReturn(patient);
        when(noteService.getNotesByPatLastName(lastName)).thenReturn(notes);

        // When
        mockMvc.perform(post("/assess/familyName")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("familyName",lastName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diabetesAssessment", is("Age: 19, Gender: F, Risk level: In Danger")))
                .andReturn();

    }


    @Test
    void testGetReportByPatLastNameShouldThrowError() throws Exception {
        // Given
        String lastName = "lastName";

        Note[] notes = {};
        when(noteService.getNotesByPatLastName(lastName)).thenReturn(notes);
        // When
        mockMvc.perform(post("/assess/familyName")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("familyName", lastName))
                .andExpect(status().isNotFound())
                .andReturn();

    }

    @Test
    void testGetReportByPatLastNameShouldThrowNoPatientError() throws Exception {
        // Given
       String lastName = "lastName";

        when(patientService.getPatientByPatLastName(lastName)).thenReturn(null);

        // When
        mockMvc.perform(post("/assess/familyName")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("familyName", lastName))
                .andExpect(status().isNotFound())
                .andReturn();

    }
}