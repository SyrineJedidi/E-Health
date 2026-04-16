package com.ehealth.patient.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PatientRegisteredEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${ehealth.messaging.exchange}")
    private String exchange;

    @Value("${ehealth.messaging.routing.patient-registered}")
    private String routingKey;

    public void publish(PatientRegisteredEvent event) {
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, event);
            log.info("[RabbitMQ] Publié patient.registered patientId={}", event.patientId());
        } catch (Exception e) {
            log.warn("[RabbitMQ] Publication inscription patient impossible : {}", e.getMessage());
        }
    }
}
