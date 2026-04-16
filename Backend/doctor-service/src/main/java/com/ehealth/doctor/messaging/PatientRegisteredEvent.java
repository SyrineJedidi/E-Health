package com.ehealth.doctor.messaging;

import java.time.Instant;

/** Même contrat JSON que patient-service (désérialisation RabbitMQ). */
public record PatientRegisteredEvent(long patientId, String email, String nom, String prenom, Instant occurredAt) {}
