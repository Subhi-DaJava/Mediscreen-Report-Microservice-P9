package com.oc.rapportmicroservice.controller;

import com.oc.rapportmicroservice.model.Report;
import com.oc.rapportmicroservice.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ReportController.class)
class ReportControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    @MockBean
    private ReportService reportService;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void testGetReportByPatIdShouldGenerateReport() throws Exception {
        // Given
        Long patId = 2L;
        Report report = new Report(patId, "Lucas Ferguson", "Age: 35, Gender: M, Risk level: Borderline");

        when(reportService.getReportByPatId(patId)).thenReturn(report);

        // When
        mockMvc.perform(post("/assess/id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("patId", String.valueOf(patId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diabetesAssessment", is("Age: 35, Gender: M, Risk level: Borderline")))
                .andReturn();
        verify(reportService).getReportByPatId(anyLong());
    }

    @Test
    void testGetReportByPatIdShouldThrowError() throws Exception {
        // Given
        Long patId = 2L;

        when(reportService.getReportByPatId(patId)).thenReturn(null);

        // When
        mockMvc.perform(post("/assess/id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("patId", String.valueOf(patId)))
                .andExpect(status().isNotFound())
                .andReturn();

        verify(reportService, times(1)).getReportByPatId(anyLong());
    }

    @Test
    void testGetReportByPatLastNameShouldGenerateReport() throws Exception {
        // Given
        Report report = new Report(3L, "John Doe", "Age: 20, Gender: F, Risk level: In Danger");

        when(reportService.getReportByPatLastName(anyString())).thenReturn(report);

        // When
        mockMvc.perform(post("/assess/familyName")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("familyName", anyString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diabetesAssessment", is("Age: 20, Gender: F, Risk level: In Danger")))
                .andReturn();

        verify(reportService, times(1)).getReportByPatLastName(anyString());

    }


    @Test
    void testGetReportByPatLastNameShouldThrowError() throws Exception {
        // Given
        String lastName = "lastName";

        when(reportService.getReportByPatLastName(lastName)).thenReturn(null);

        // When
        mockMvc.perform(post("/assess/familyName")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("familyName", lastName))
                .andExpect(status().isNotFound())
                .andReturn();

        verify(reportService, times(1)).getReportByPatLastName(anyString());

    }
}