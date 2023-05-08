package com.oc.rapportmicroservice.service;

import com.oc.rapportmicroservice.model.Report;

public interface ReportService {
    Report getReportByPatId(Long patientId);

    Report getReportByPatLastName(String patientName);
}
