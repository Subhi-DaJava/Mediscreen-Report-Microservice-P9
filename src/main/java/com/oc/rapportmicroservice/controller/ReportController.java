package com.oc.rapportmicroservice.controller;

import com.oc.rapportmicroservice.exception.ResourceNotFoundException;
import com.oc.rapportmicroservice.model.Report;
import com.oc.rapportmicroservice.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/assess")
public class ReportController {
    private static final  Logger logger = LoggerFactory.getLogger(ReportController.class);

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/id")
    public ResponseEntity<Report> getReportByPatId(@RequestParam Long patId) {
        logger.debug("gerReportByPatId starts here from ReportController");
        Report report = reportService.getReportByPatId(patId);
        if (report == null) {
            logger.debug("No report generated, cause No resource related");
            throw new ResourceNotFoundException("No resource related!");
        }
        logger.info("Report for the patient with id:{} has been successfully generated, from ReportController", patId);
        return ResponseEntity.ok(report);
    }

    @PostMapping("/familyName")
    public ResponseEntity<Report> getReportByPatLastName(@RequestParam String familyName) {
        logger.debug("gerReportByPatLastName starts here from ReportController");
        Report report = reportService.getReportByPatLastName(familyName);
        if (report == null) {
            logger.debug("No report generated, cause No resource related");
            throw new ResourceNotFoundException("No resource related!");
        }
        logger.info("Report for the patient with lastName:{} has been successfully generated, from ReportController", familyName);
        return ResponseEntity.ok(report);
    }
}
