package com.oc.rapportmicroservice.service;

import com.oc.rapportmicroservice.model.Note;

public interface NoteService {
    Note[] getNotesByPatId(Long patientId);
    Note[] getNotesByPatLastName(String patientName);
}
