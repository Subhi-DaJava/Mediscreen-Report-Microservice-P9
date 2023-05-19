package com.oc.rapportmicroservice.service;

import com.oc.rapportmicroservice.exception.ResourceNotFoundException;
import com.oc.rapportmicroservice.model.Note;

import com.oc.rapportmicroservice.service.implement.NoteServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoteServiceImplTest {
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private NoteServiceImpl noteService;

    @Value("${API_NOTES_DOCKER_PAT_ID}")
    private String noteUrlDockerByPatId;

    @Value("${API_NOTES_DOCKER_PAT_NAME}")
    private String noteUrlDockerByPatName;

    @Test
    void testGetNotesByPatId() {
        // Given
        Long patientId = 5L;
        Note[] notes = {
                new Note(5L, "Doe", "Comment 1", LocalDate.now()),
                new Note(5L, "Doe", "Comment 2", LocalDate.now())
        };

        when(restTemplate.getForObject(noteUrlDockerByPatId, Note[].class, patientId)).thenReturn(notes);

        // When
        Note[] notesLoaded = noteService.getNotesByPatId(patientId);

        // Then
        assertThat(notesLoaded.length).isEqualTo(2);

    }

    @Test
    void testGetNotesByPatIdShouldThrowError() {
        // Given
        Long patientId = 5L;
        Note[] notes = {};

        when(restTemplate.getForObject(noteUrlDockerByPatId, Note[].class, patientId)).thenReturn(notes);

        // Then
        assertThatThrownBy(() -> noteService.getNotesByPatId(patientId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining( "No notes found for the patient in the database.");

        verify(restTemplate, times(1)).getForObject(noteUrlDockerByPatId, Note[].class, patientId);

    }

    @Test
    void testGetNotesByPatLastName() {
       // Given
        Note[] notes = {
                new Note(5L, "Doe", "Comment 1", LocalDate.now()),
                new Note(5L, "Doe", "Comment 2", LocalDate.now())
        };

        when(restTemplate.getForObject(noteUrlDockerByPatName, Note[].class, "Doe")).thenReturn(notes);

        // When
        Note[] notesLoaded = noteService.getNotesByPatLastName("Doe");

        // Then
        assertThat(notesLoaded.length).isEqualTo(2);
    }

    @Test
    void testGetNotesByPatLastNameShouldThrowError() {
        // Given
        String lastName = "Doe";
        Note[] notes = {};

        when(restTemplate.getForObject(noteUrlDockerByPatName, Note[].class, lastName)).thenReturn(notes);

        // Then
        assertThatThrownBy(() -> noteService.getNotesByPatLastName("Doe"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No notes found for last name: " + lastName + " in the database.");

        verify(restTemplate, times(1)).getForObject(noteUrlDockerByPatName, Note[].class, lastName);

    }
}