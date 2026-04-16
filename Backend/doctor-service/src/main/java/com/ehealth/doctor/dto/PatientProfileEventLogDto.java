package com.ehealth.doctor.dto;

import com.ehealth.doctor.model.PatientProfileEventLog;

import java.time.Instant;

/** Réponse API pour l’audit (évite les soucis de sérialisation Hibernate / JSON). */
public record PatientProfileEventLogDto(
        Long id,
        Long patientId,
        String email,
        String summary,
        Instant receivedAt,
        String correlationId,
        String clinicalAlertLevel,
        String initiatedBy) {

    public static PatientProfileEventLogDto fromEntity(PatientProfileEventLog e) {
        return new PatientProfileEventLogDto(
                e.getId(),
                e.getPatientId(),
                e.getEmail(),
                e.getSummary(),
                e.getReceivedAt(),
                e.getCorrelationId(),
                e.getClinicalAlertLevel(),
                e.getInitiatedBy());
    }
}
