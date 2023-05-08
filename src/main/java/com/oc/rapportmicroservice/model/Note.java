package com.oc.rapportmicroservice.model;

import java.time.LocalDate;

public record Note(String id, Long patId, String patLastName, String comment, LocalDate createdAt) {
}
