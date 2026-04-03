package com.ehealth.patient.client;

import com.ehealth.patient.dto.RendezVousDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "appointment-service", fallback = AppointmentClientFallback.class)
public interface AppointmentClient {

    @GetMapping("/api/rendezvous/patient/{patientId}")
    List<RendezVousDTO> getRendezVousByPatientId(@PathVariable("patientId") Long patientId);
}
