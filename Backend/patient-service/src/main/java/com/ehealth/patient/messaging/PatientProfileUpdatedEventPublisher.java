package com.ehealth.patient.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PatientProfileUpdatedEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${ehealth.messaging.exchange}")
    private String exchange;

    @Value("${ehealth.messaging.routing.patient-profile-updated}")
    private String routingKey;

    public void publish(PatientProfileUpdatedEvent event) {
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, event);
            log.info(
                    "[RabbitMQ] Publié patient.profile.updated id={} correlationId={}",
                    event.patientId(),
                    event.correlationId());
        } catch (Exception e) {
            log.warn("[RabbitMQ] Publication profil patient impossible : {}", e.getMessage());
        }
    }
}
