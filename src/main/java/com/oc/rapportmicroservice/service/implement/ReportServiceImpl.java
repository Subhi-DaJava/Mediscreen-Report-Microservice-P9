package com.oc.rapportmicroservice.service.implement;

import com.oc.rapportmicroservice.exception.ResourceNotFoundException;
import com.oc.rapportmicroservice.model.Note;
import com.oc.rapportmicroservice.model.Patient;
import com.oc.rapportmicroservice.model.Report;
import com.oc.rapportmicroservice.service.NoteService;
import com.oc.rapportmicroservice.service.PatientService;
import com.oc.rapportmicroservice.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {
    private static final Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);

    private final PatientService patientService;
    private final NoteService noteService;

    public ReportServiceImpl(PatientService patientService, NoteService noteService) {
        this.patientService = patientService;
        this.noteService = noteService;
    }

    @Override
    public Report getReportByPatId(Long patientId) {
        logger.debug("getReportByPatId method starts here, from ReportServiceImpl");

        //Patient patient = restTemplate.getForObject(patientUrlLocalByPatId, Patient.class, patientId);
        Patient patient = patientService.getPatientByPatId(patientId);

        if(patient == null) {
            logger.error("No notes found for PatientId: {}", patientId);
            throw new ResourceNotFoundException("No Patient found in the database.");
        }

        //Note[] notes = restTemplate.getForObject(noteUrlLocalByPatId, Note[].class, patientId);
        Note[] notes = noteService.getNotesByPatId(patientId);

        if (notes == null || notes.length == 0) {
            logger.error("No notes found for PatientId: {}", patientId);
            throw new ResourceNotFoundException("No notes found for patient id: " + patientId + " in the database.");
        }

        logger.info("Report for patient with id:{} has been successfully generated, from getReportByPatId method, in ReportServiceImpl", patientId);

        return analyzeNotes(patient, notes);
    }

    @Override
    public Report getReportByPatLastName(String patientName) {
        logger.debug("getReportByPatLastName method starts here, from ReportController");
        //Patient patient = restTemplate.getForObject(patientUrlLocalByPatName, Patient.class, patientName);
        Patient patient = patientService.getPatientByPatLastName(patientName);

        if(patient == null) {
            logger.error("No notes found for PatientId: {}", patientName);
            throw new ResourceNotFoundException("No Patient found in the database.");
        }

        //Note[] notes = restTemplate.getForObject(noteUrlLocalByPatName, Note[].class, patientName);
        Note[] notes = noteService.getNotesByPatLastName(patientName);

        if (notes == null || notes.length == 0) {
            logger.error("No notes found for PatientLastName: {}", patientName);
            throw new ResourceNotFoundException("No notes found for patient lastName: " + patientName + " in the database.");
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

        Report report = getReport(patient, riskLevel, age);

        logger.info("analyzeNotes method has been successfully called and, from ReportServiceImpl");
        return report;
    }

    private static Report getReport(Patient patient, String riskLevel, long age) {
        logger.debug("getReport method get called, from ReportController");
        Report report = new Report();
        report.setPatId(patient.id());
        report.setPatFullName(patient.firstName() + " " + patient.lastName());

        report.setDiabetesAssessment("Age: %d, Gender: %s, Risk level: %s".formatted(age, patient.sex(), riskLevel));

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

    public long countTriggerWords(List<String> comments) {
        logger.debug("countTriggerWords method starts here, from ReportController");
        List<String> triggerWords = Arrays.asList(
                "hémoglobine a1c", "microalbumine", "taille", "poids", "fumeur", "anormal", "cholestérol", "vertige", "rechute", "réaction", "anticorps");
        //"BODY HEIGHT", "BODY WEIGHT", "ABNORMAL", "REACTION", "SMOKER", "CHOLESTEROL", "ANTIBODIES", "HEMOGLOBIN A1C", "DIZZINESS", "MICRO ALBUMIN", "RELAPSE");

        long count = comments.stream()
                .map(String::toLowerCase)
                .flatMap(comment -> triggerWords.stream().filter(comment::contains))
                .distinct()
                .peek(System.out::println)
                .count();

        System.out.println("The number of occurrences of the Trigger Words: " + count);

        logger.info("countTriggerWords method has been successfully called and calculated the number of trigger words: {}, from ReportServiceImpl", count);
        return count;
    }

    public String calculateRiskLevel(long age, String gender, long numTriggers) {
        if (numTriggers == 0) {
            return "None";
        } else if (numTriggers >= 2 && age >= 30 && numTriggers < 6) {
            return "Borderline";
        } else if (gender.equals("M") && age <= 30 && (numTriggers == 3 || numTriggers == 4)) {
            return "In Danger";
        } else if (gender.equals("F") && age <= 30 && (numTriggers >= 4 && numTriggers < 7)) {
            return "In Danger";
        } else if (age > 30 && numTriggers >= 6 && numTriggers < 8) {
            return "In Danger";
        } else if (gender.equals("M") && age <= 30 && numTriggers >= 5) {
            return "Early onset";
        } else if (gender.equals("F") && (age <= 30 && numTriggers >= 7)) {
            return "Early onset";
        } else if (age > 30 && numTriggers >= 8) {
            return "Early onset";
        } else {
            return "Unknown";
        }
    }

}