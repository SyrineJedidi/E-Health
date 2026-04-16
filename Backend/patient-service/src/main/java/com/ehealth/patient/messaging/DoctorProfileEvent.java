package com.ehealth.patient.messaging;

import java.time.Instant;

/** Même contrat JSON que doctor-service (désérialisation RabbitMQ). */
public record DoctorProfileEvent(
        long doctorId,
        String email,
        String nom,
        String prenom,
        String specialtyCode,
        String eventType,
        Instant occurredAt) {}
