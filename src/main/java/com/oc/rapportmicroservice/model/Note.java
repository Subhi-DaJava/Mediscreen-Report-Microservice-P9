package com.oc.rapportmicroservice.model;

import java.time.LocalDate;

public record Note(Long patId, String patLastName, String comment, LocalDate createdAt) {
}
