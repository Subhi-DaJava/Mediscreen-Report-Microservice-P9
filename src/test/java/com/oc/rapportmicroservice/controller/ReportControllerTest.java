package com.oc.rapportmicroservice.controller;

import com.oc.rapportmicroservice.model.Report;
import com.oc.rapportmicroservice.service.ReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

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

    @Test
    void getReportByPatId() throws Exception {
        // Given
        Report report = new Report(2L, "Lucas Ferguson", "Age: 35, Gender: M, Risk level: Borderline");

        when(reportService.getReportByPatId(2L)).thenReturn(report);

        // When
       mockMvc.perform(post("/assess/id")
                       .contentType(MediaType.APPLICATION_JSON)
                       .param("patId", String.valueOf(2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diabetesAssessment", is("Age: 35, Gender: M, Risk level: Borderline")))
                .andReturn();

    }

    @Test
    void getReportByPatLastName() throws Exception {
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
    }
}