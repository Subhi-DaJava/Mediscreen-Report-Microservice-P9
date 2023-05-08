package com.oc.rapportmicroservice.model;

import java.time.LocalDate;

public record Patient(Long id, String lastName, String firstName, LocalDate dateOfBirth, String sex, String homeAddress, String phoneNumber) {
}
