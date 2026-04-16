package com.ehealth.doctor.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Files doctor-service : inscription patient + mise à jour de dossier (profil).
 */
@Configuration
public class DoctorRabbitConfig {

    @Bean
    public TopicExchange ehealthTopicExchange(@Value("${ehealth.messaging.exchange}") String name) {
        return new TopicExchange(name, true, false);
    }

    @Bean
    public Queue doctorIngestPatientQueue(@Value("${ehealth.messaging.queues.doctor-ingest-patient}") String name) {
        return new Queue(name, true);
    }

    @Bean
    public Binding doctorIngestPatientBinding(
            Queue doctorIngestPatientQueue,
            TopicExchange ehealthTopicExchange,
            @Value("${ehealth.messaging.routing.patient-registered}") String routingKey) {
        return BindingBuilder.bind(doctorIngestPatientQueue).to(ehealthTopicExchange).with(routingKey);
    }

    @Bean
    public Queue doctorIngestPatientProfileQueue(
            @Value("${ehealth.messaging.queues.doctorIngestPatientProfile}") String name) {
        return new Queue(name, true);
    }

    @Bean
    public Binding doctorIngestPatientProfileBinding(
            Queue doctorIngestPatientProfileQueue,
            TopicExchange ehealthTopicExchange,
            @Value("${ehealth.messaging.routing.patient-profile-updated}") String routingKey) {
        return BindingBuilder.bind(doctorIngestPatientProfileQueue).to(ehealthTopicExchange).with(routingKey);
    }
}
