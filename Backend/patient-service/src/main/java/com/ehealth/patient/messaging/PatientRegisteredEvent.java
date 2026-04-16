package com.ehealth.patient.messaging;

import java.time.Instant;

/**
 * Message publié lors de la création d’un patient (consommé par doctor-service).
 */
public record PatientRegisteredEvent(long patientId, String email, String nom, String prenom, Instant occurredAt) {}
