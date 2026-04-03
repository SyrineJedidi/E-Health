package com.ehealth.patient.client;

import com.ehealth.patient.dto.RendezVousDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class AppointmentClientFallback implements AppointmentClient {

    @Override
    public List<RendezVousDTO> getRendezVousByPatientId(Long patientId) {
        log.warn("appointment-service indisponible : retour d'une liste vide pour le patient id={}", patientId);
        return Collections.emptyList();
    }
}
