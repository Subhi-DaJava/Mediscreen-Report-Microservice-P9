package com.oc.rapportmicroservice.service.implement;

import com.oc.rapportmicroservice.exception.ResourceNotFoundException;
import com.oc.rapportmicroservice.model.Note;
import com.oc.rapportmicroservice.service.NoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NoteServiceImpl implements NoteService {
    private static final Logger logger = LoggerFactory.getLogger(NoteServiceImpl.class);

    private final RestTemplate restTemplate;

    @Value("${API_NOTES_DOCKER_PAT_ID}")
    private String noteUrlDockerByPatId;

    @Value("${API_NOTES_DOCKER_PAT_NAME}")
    private String noteUrlDockerByPatName;

    public NoteServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    @Override
    public Note[] getNotesByPatId(Long patientId) {
        logger.debug("getNotesByPatientId method starts here, from NoteServiceImpl");

        Note[] notes = restTemplate.getForObject(noteUrlDockerByPatId, Note[].class, patientId);

        if (notes == null || notes.length == 0) {
            logger.error("No notes found for PatientId: {}", patientId);
            throw new ResourceNotFoundException( "No notes found for the patient in the database.");
        }

        logger.info("Notes for PatientId {} have been successfully retrieved, from getNotesByPatientId method, in NoteServiceImpl", patientId);

        return notes;
    }

    @Override
    public Note[] getNotesByPatLastName(String lastName) {
        logger.debug("getNotesByPatientLastName method starts here, from NoteServiceImpl");

        Note[] notes = restTemplate.getForObject(noteUrlDockerByPatName, Note[].class, lastName);

        if (notes == null || notes.length == 0) {
            logger.error("No notes found for LastName: {}", lastName);
            throw new ResourceNotFoundException("No notes found for last name: " + lastName + " in the database.");
        }

        logger.info("Notes for LastName {} have been successfully retrieved, from getNotesByPatientLastName method, in NoteServiceImpl", lastName);

        return notes;
    }
}
