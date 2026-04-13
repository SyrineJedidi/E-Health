package com.ehealth.prescription.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "patient-service",
        contextId = "prescriptionPatientClient",
        configuration = FeignForwardAuthConfig.class)
public interface PatientFeignClient {

    @GetMapping("/api/patients/{id}")
    PatientApiEnvelope getPatient(@PathVariable("id") Long id);

    @GetMapping("/api/patients/lookup-email")
    PatientApiEnvelope lookupPatientByEmail(@RequestParam("e") String email);
}
