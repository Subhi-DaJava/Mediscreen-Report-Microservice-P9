package com.oc.rapportmicroservice.controller;

import com.oc.rapportmicroservice.model.Report;
import com.oc.rapportmicroservice.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/report")
public class ReportController {
    private static final  Logger logger = LoggerFactory.getLogger(ReportController.class);

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/by-id/{patId}")
    public ResponseEntity<Report> getReportByPatId(@PathVariable Long patId) {
        logger.debug("gerReportByPatId starts here from ReportController");
        Report report = reportService.getReportByPatId(patId);
        logger.info("Report for the patient with id:{} has been successfully generated, from ReportController", patId);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/by-name/{patLastName}")
    public ResponseEntity<Report> getReportByPatLastName(@PathVariable String patLastName) {
        logger.debug("gerReportByPatLastName starts here from ReportController");
        Report report = reportService.getReportByPatLastName(patLastName);
        logger.info("Report for the patient with lastName:{} has been successfully generated, from ReportController", patLastName);
        return ResponseEntity.ok(report);
    }
}
