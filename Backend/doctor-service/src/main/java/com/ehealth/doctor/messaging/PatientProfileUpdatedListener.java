package com.ehealth.doctor.messaging;

import com.ehealth.doctor.model.PatientProfileEventLog;
import com.ehealth.doctor.repository.PatientProfileEventLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.stream.Collectors;

/**
 * Scénario métier : le service patient notifie les mises à jour de dossier (coordonnées, données de santé de base)
 * pour que le périmètre « médecin » puisse tenir une piste d’audit sans appel HTTP synchrone.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PatientProfileUpdatedListener {

    private final PatientProfileEventLogRepository patientProfileEventLogRepository;

    @RabbitListener(queues = "${ehealth.messaging.queues.doctorIngestPatientProfile}")
    @Transactional
    public void onPatientProfileUpdated(PatientProfileUpdatedEvent event) {
        String attrs =
                event.changedAttributes() == null
                        ? ""
                        : event.changedAttributes().stream().collect(Collectors.joining(", "));
        String by = event.initiatedBy() != null ? event.initiatedBy() : "UNKNOWN";
        String actorLabel = labelForInitiator(by);
        log.info(
                "[RabbitMQ] {} — id={} email={} alerte={} champs=[{}] correlationId={}",
                actorLabel,
                event.patientId(),
                event.email(),
                event.clinicalAlertLevel(),
                attrs,
                event.correlationId());

        String summary =
                String.format(
                        "%s — Patient #%d — champs: [%s] — %s",
                        actorLabel, event.patientId(), attrs, event.clinicalAlertLevel());
        patientProfileEventLogRepository.save(
                PatientProfileEventLog.builder()
                        .patientId(event.patientId())
                        .email(event.email())
                        .summary(summary)
                        .receivedAt(event.occurredAt() != null ? event.occurredAt() : Instant.now())
                        .correlationId(event.correlationId())
                        .clinicalAlertLevel(event.clinicalAlertLevel())
                        .initiatedBy(by)
                        .build());
    }

    private static String labelForInitiator(String role) {
        if (role == null || role.isBlank()) {
            return "Mise à jour du profil (origine inconnue)";
        }
        return switch (role.trim().toUpperCase()) {
            case "PATIENT" -> "Le patient a modifié son profil";
            case "DOCTOR" -> "Un médecin a modifié le profil de ce patient";
            case "ADMIN" -> "Un administrateur a modifié le profil";
            default -> "Profil mis à jour (" + role + ")";
        };
    }
}
