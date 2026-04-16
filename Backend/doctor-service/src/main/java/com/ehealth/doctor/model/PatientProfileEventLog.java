package com.ehealth.doctor.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Audit des événements « profil patient mis à jour » reçus via RabbitMQ (démonstration traçabilité inter-services).
 */
@Entity
@Table(name = "patient_profile_event_log")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientProfileEventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long patientId;

    private String email;

    /** Résumé lisible (champs modifiés, niveau d’alerte). */
    @Column(length = 2000)
    private String summary;

    @Column(nullable = false)
    private Instant receivedAt;

    private String correlationId;

    private String clinicalAlertLevel;

    /** PATIENT, DOCTOR, ADMIN — auteur de la mise à jour côté API patient-service. */
    @Column(length = 32)
    private String initiatedBy;
}
