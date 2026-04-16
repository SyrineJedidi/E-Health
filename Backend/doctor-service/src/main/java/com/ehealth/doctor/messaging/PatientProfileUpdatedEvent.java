package com.ehealth.doctor.messaging;

import java.time.Instant;
import java.util.List;

/** Même contrat JSON que patient-service. */
public record PatientProfileUpdatedEvent(
        long patientId,
        String email,
        String nom,
        String prenom,
        String eventType,
        List<String> changedAttributes,
        String clinicalAlertLevel,
        Instant occurredAt,
        String source,
        String initiatedBy,
        String correlationId) {}
