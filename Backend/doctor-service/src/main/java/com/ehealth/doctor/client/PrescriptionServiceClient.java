package com.ehealth.doctor.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
        name = "prescription-service",
        contextId = "prescriptionServiceClient",
        configuration = PatientFeignConfig.class)
public interface PrescriptionServiceClient {

    @GetMapping("/api/prescriptions/doctor/{doctorId}")
    List<PrescriptionBriefJson> listByDoctor(@PathVariable("doctorId") Long doctorId);
}
