package com.ehealth.patient.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Topologie Rabbit : échange + files partagées avec doctor-service (noms identiques = idempotent).
 * La file {@code q.doctor.ingest.patient.profile} est aussi déclarée ici pour qu’un message
 * publié après un PUT patient ne soit pas perdu lorsque doctor-service n’est pas encore démarré.
 */
@Configuration
public class PatientRabbitConfig {

    @Bean
    public TopicExchange ehealthTopicExchange(@Value("${ehealth.messaging.exchange}") String name) {
        return new TopicExchange(name, true, false);
    }

    @Bean
    public Queue patientIngestDoctorQueue(@Value("${ehealth.messaging.queues.patient-ingest-doctor}") String name) {
        return new Queue(name, true);
    }

    @Bean
    public Binding patientIngestDoctorBinding(
            Queue patientIngestDoctorQueue,
            TopicExchange ehealthTopicExchange,
            @Value("${ehealth.messaging.routing.doctor-profile}") String routingKey) {
        return BindingBuilder.bind(patientIngestDoctorQueue).to(ehealthTopicExchange).with(routingKey);
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
