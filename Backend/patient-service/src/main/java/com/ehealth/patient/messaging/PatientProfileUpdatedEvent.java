package com.ehealth.patient.messaging;

import java.time.Instant;
import java.util.List;

/**
 * Événement domaine : mise à jour de la fiche patient (coordonnées, données médicales de base).
 * Consommé par doctor-service pour prise en compte côté praticiens / traçabilité.
 */
public record PatientProfileUpdatedEvent(
        long patientId,
        String email,
        String nom,
        String prenom,
        String eventType,
        List<String> changedAttributes,
        /** MISE_A_JOUR_ROUTINE ou VIGILANCE_CLINIQUE (antécédents / groupe sanguin). */
        String clinicalAlertLevel,
        Instant occurredAt,
        String source,
        /** PATIENT, DOCTOR ou ADMIN — issu du JWT (claim {@code role}). */
        String initiatedBy,
        String correlationId) {}
