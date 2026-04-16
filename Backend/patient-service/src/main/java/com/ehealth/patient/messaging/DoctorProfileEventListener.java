package com.ehealth.patient.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Scénario 1 (async) : <strong>doctor-service → RabbitMQ → patient-service</strong>.
 * Déclenché par POST/PUT médecin côté doctor (voir logs ici après création fiche).
 */
@Component
@Slf4j
public class DoctorProfileEventListener {

    @RabbitListener(queues = "${ehealth.messaging.queues.patient-ingest-doctor}")
    public void onDoctorProfile(DoctorProfileEvent event) {
        log.info(
                "[RabbitMQ] Reçu événement médecin — {} {} | email={} | spé={} | type={} | id={}",
                event.prenom(),
                event.nom(),
                event.email(),
                event.specialtyCode(),
                event.eventType(),
                event.doctorId());
    }
}
