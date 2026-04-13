package com.ehealth.doctor.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(
        name = "patient-service",
        contextId = "patientServiceClient",
        configuration = PatientFeignConfig.class
)
public interface PatientServiceClient {

    /** L'en-tête {@code Authorization} est ajouté par {@link PatientFeignConfig}. */
    @GetMapping("/api/patients")
    PatientListResponse getAllPatients();

    @PostMapping("/api/patients/batch")
    PatientListResponse getPatientBatch(@RequestBody List<Long> ids);
}
