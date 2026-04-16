package com.ehealth.doctor.messaging;

import java.time.Instant;

/**
 * Message publié lors d’une création / mise à jour de fiche médecin (consommé par patient-service).
 */
public record DoctorProfileEvent(
        long doctorId,
        String email,
        String nom,
        String prenom,
        String specialtyCode,
        String eventType,
        Instant occurredAt) {}
