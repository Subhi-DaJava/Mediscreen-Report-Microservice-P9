package com.oc.rapportmicroservice.service.implement;

import com.oc.rapportmicroservice.exception.ResourceNotFoundException;
import com.oc.rapportmicroservice.model.Note;
import com.oc.rapportmicroservice.model.Patient;
import com.oc.rapportmicroservice.model.Report;
import com.oc.rapportmicroservice.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {
    private static final Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);

    private final RestTemplate restTemplate;

    public ReportServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Report getReportByPatId(Long patientId) {
        logger.debug("getReportByPatId method starts here, from ReportServiceImpl");

        //Patient patient = restTemplate.getForObject("http://localhost:8081/api/patients/{id}", Patient.class, patientId);
        Patient patient = restTemplate.getForObject("http://patient-microservice:8081/api/patients/{id}", Patient.class, patientId);

        //Note[] notes = restTemplate.getForObject("http://localhost:8082/api/notes/by-patId/{patId}", Note[].class, patientId);
        Note[] notes = restTemplate.getForObject("http://note-microservice:8082/api/notes/by-patId/{patId}", Note[].class, patientId);

        if (patient == null) {
            logger.error("No Patient found with this PatientId:{%d}".formatted(patientId));
            throw new ResourceNotFoundException("No Patient found with this patient id:{%d} in the database.".formatted(patientId));
        }

        logger.info("Report for patient with id:{} has been successfully generated, from getReportByPatId method, in ReportServiceImpl", patientId);

        return analyzeNotes(patient, notes);
    }

    @Override
    public Report getReportByPatLastName(String patientName) {
        logger.debug("getReportByPatLastName method starts here, from ReportController");
        //Patient patient = restTemplate.getForObject("http://localhost:8081/api/patient?lastName={lastName}", Patient.class, patientName);
        Patient patient = restTemplate.getForObject("http://patient-microservice:8081/api/patient?lastName={lastName}", Patient.class, patientName);

        //Note[] notes = restTemplate.getForObject("http://localhost:8082/api/notes/by-lastName/{lastName}", Note[].class, patientName);
        Note[] notes = restTemplate.getForObject("http://notre-microservice:8082/api/notes/by-lastName/{lastName}", Note[].class, patientName);

        if (patient == null) {
            logger.error("No Patient found with this PatientLastName:{%s}".formatted(patientName));
            throw new ResourceNotFoundException("No Patient found with this patient id:{%s} in the database.".formatted(patientName));
        }

        logger.info("Report for patient with lastName:{} has been successfully generated, from getReportPatLastName method, in ReportServiceImpl", patientName);

        return analyzeNotes(patient, notes);
    }

    private Report analyzeNotes(Patient patient, Note[] notes) {
        logger.debug("analyzeNotes method get called, from ReportServiceImpl");
        String riskLevel;
        long termsTrigger;
        long age;

        age = calculateAgeWithChronoUnit(patient.dateOfBirth());

        List<String> comments = Arrays.stream(notes).map(Note::comment).toList();

        termsTrigger = countTriggerWords(comments);

        riskLevel = calculateRiskLevel(age, patient.sex(), termsTrigger);

        Report report = getReport(patient, riskLevel, termsTrigger, age);

        logger.info("analyzeNotes method has been successfully called and, from ReportServiceImpl");
        return report;
    }

    private static Report getReport(Patient patient, String riskLevel, long termsTrigger, long age) {
        logger.debug("getReport method get called, from ReportController");
        Report report = new Report();
        report.setPatId(patient.id());
        report.setPatFullName(patient.firstName() + " " + patient.lastName());

        report.setDiabetesAssessment("(Age: %d, Gender: %s, Trigger words: %d, Risk level: %s)".formatted(age, patient.sex(), termsTrigger, riskLevel));

        logger.info("getReport method has been successfully called and generated the report, from ReportServiceImpl");
        return report;
    }

    private int calculateAge(LocalDate dateOfBirth) {
        logger.debug("calculateAgeWithChronoUnit method starts here, from ReportServiceImpl");
        Period diff = Period.between(dateOfBirth, LocalDate.now());
        return diff.getYears();
    }

    private long calculateAgeWithChronoUnit(LocalDate datOfBirth) {
        logger.debug("calculateAgeWithChronoUnit method starts here, from ReportServiceImpl");
        return ChronoUnit.YEARS.between(datOfBirth, LocalDate.now());
    }

    private long countTriggerWords(List<String> comments) {
        logger.debug("countTriggerWords method starts here, from ReportController");
        List<String> triggerWords = Arrays.asList(
                "hémoglobine", "microalbumine", "taille", "poids", "fumeur", "anormal", "cholestérol", "vertige", "rechute", "réaction", "anticorps", "anormale", "anormales", "anormaux");

        long count = comments.stream()
                .map(String::toLowerCase)
                .map(comment -> comment.replaceAll("[^a-zA-ZÀ-ÖØ-öø-ÿ0-9\\s]+", " ").replaceAll("\n", " ").replaceAll("'", " "))
                .flatMap(comment -> Arrays.stream(comment.split("\\s+")))
                .distinct()
                .filter(triggerWords::contains)
                .peek(System.out::println)
                .count();

        System.out.println("The number of the occurrences of the Trigger Words : " + count);

        logger.info("countTriggerWords method has been successfully called and calculate the number of trigger words:{}, from ReportServiceImpl", count);
        return count;
    }

    public String calculateRiskLevel(long age, String gender, long numTriggers) {
        logger.debug("calculateRiskLevel method starts here, from ReportServiceImpl");
        if (numTriggers >= 0 && numTriggers < 2) {
            return "None";
        } else if (numTriggers >= 2 && age >= 30 && numTriggers < 6) {
            return "Borderline";
        } else if ((gender.equals("M") && age <= 30 && numTriggers >= 3 && numTriggers <= 4) ||
                (gender.equals("F") && age <= 30 && numTriggers >= 4 && numTriggers <= 6) || (age > 30 && numTriggers >= 6 && numTriggers <= 7)) {
            return "In Danger";
        } else if ((gender.equals("M") && age <= 30 && numTriggers >= 5) ||
                (gender.equals("F") && age <= 30 && numTriggers >= 7) || (age > 30 && numTriggers >= 8)) {
            return "Early onset";
        } else {
            return "Unkown";
        }
    }
}