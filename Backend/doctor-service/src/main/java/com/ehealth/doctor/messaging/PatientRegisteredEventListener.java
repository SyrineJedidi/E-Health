package com.ehealth.doctor.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Scénario 2 (async) : <strong>patient-service → RabbitMQ → doctor-service</strong>.
 * Déclenché par POST patient côté patient-service (voir logs ici après création).
 */
@Component
@Slf4j
public class PatientRegisteredEventListener {

    @RabbitListener(queues = "${ehealth.messaging.queues.doctor-ingest-patient}")
    public void onPatientRegistered(PatientRegisteredEvent event) {
        log.info(
                "[RabbitMQ] Reçu inscription patient — {} {} | email={} | id={}",
                event.prenom(),
                event.nom(),
                event.email(),
                event.patientId());
    }
}
