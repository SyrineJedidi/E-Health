package com.ehealth.doctor.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "appointment-service",
        contextId = "appointmentFeignClient",
        configuration = PatientFeignConfig.class)
public interface AppointmentFeignClient {

    @GetMapping("/api/rendezvous/doctor/{doctorId}")
    AppointmentApiEnvelope listByDoctor(@PathVariable("doctorId") Long doctorId);
}
