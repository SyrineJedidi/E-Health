package com.ehealth.prescription.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "doctor-service",
        contextId = "prescriptionDoctorClient",
        configuration = FeignForwardAuthConfig.class)
public interface DoctorFeignClient {

    @GetMapping("/api/doctors/{id}")
    DoctorApiEnvelope getDoctor(@PathVariable("id") Long id);
}
