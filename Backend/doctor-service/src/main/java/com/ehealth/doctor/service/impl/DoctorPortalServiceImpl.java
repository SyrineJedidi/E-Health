package com.ehealth.doctor.service.impl;

import com.ehealth.doctor.client.AppointmentApiEnvelope;
import com.ehealth.doctor.client.AppointmentFeignClient;
import com.ehealth.doctor.client.PrescriptionBriefJson;
import com.ehealth.doctor.client.PrescriptionServiceClient;
import com.ehealth.doctor.client.RendezVousBriefJson;
import com.ehealth.doctor.dto.DoctorDTO;
import com.ehealth.doctor.dto.PatientSummaryDTO;
import com.ehealth.doctor.dto.RendezVousViewDTO;
import com.ehealth.doctor.model.Doctor;
import com.ehealth.doctor.client.PatientListResponse;
import com.ehealth.doctor.client.PatientServiceClient;
import com.ehealth.doctor.repository.DoctorRepository;
import com.ehealth.doctor.service.DoctorPortalService;
import com.ehealth.doctor.service.DoctorService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorPortalServiceImpl implements DoctorPortalService {

    private final DoctorRepository doctorRepository;
    private final DoctorService doctorService;
    private final PrescriptionServiceClient prescriptionServiceClient;
    private final PatientServiceClient patientServiceClient;
    private final AppointmentFeignClient appointmentFeignClient;

    @Override
    @Transactional(readOnly = true)
    public DoctorDTO getCurrentDoctor() {
        Doctor doc = requireCurrentDoctor();
        return doctorService.findById(doc.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientSummaryDTO> getMyPatients() {
        DoctorDTO me = getCurrentDoctor();
        Set<Long> patientIds = new LinkedHashSet<>();

        try {
            List<PrescriptionBriefJson> rx = prescriptionServiceClient.listByDoctor(me.getId());
            if (rx != null) {
                for (PrescriptionBriefJson p : rx) {
                    if (p.getPatientId() != null) {
                        patientIds.add(p.getPatientId());
                    }
                }
            }
        } catch (FeignException e) {
            log.warn("Feign prescription-service (ordonnances médecin) status={}", e.status());
        }

        try {
            AppointmentApiEnvelope env = appointmentFeignClient.listByDoctor(me.getId());
            if (env != null && env.getData() != null) {
                for (RendezVousBriefJson a : env.getData()) {
                    if (a.getPatientId() != null) {
                        patientIds.add(a.getPatientId());
                    }
                }
            }
        } catch (FeignException e) {
            log.debug("Feign appointment-service (RDV médecin) indisponible status={}", e.status());
        }

        if (patientIds.isEmpty()) {
            return List.of();
        }

        try {
            PatientListResponse batch = patientServiceClient.getPatientBatch(new ArrayList<>(patientIds));
            if (batch != null && batch.getData() != null) {
                return batch.getData();
            }
        } catch (FeignException e) {
            log.warn("Feign patient-service batch status={}", e.status());
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY, "Impossible de charger les fiches patients.");
        }
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVousViewDTO> getMyAppointments() {
        DoctorDTO me = getCurrentDoctor();
        try {
            AppointmentApiEnvelope env = appointmentFeignClient.listByDoctor(me.getId());
            if (env != null && env.getData() != null) {
                return env.getData().stream().map(this::toRendezVousView).toList();
            }
        } catch (FeignException e) {
            log.debug("Rendez-vous médecin non disponibles status={}", e.status());
        }
        return List.of();
    }

    private Doctor requireCurrentDoctor() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Non authentifié");
        }
        return doctorRepository
                .findByEmailIgnoreCase(email.trim())
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Aucune fiche médecin pour ce compte. Utilisez le même email que dans"
                                                + " l’annuaire médecins."));
    }

    private RendezVousViewDTO toRendezVousView(RendezVousBriefJson a) {
        return RendezVousViewDTO.builder()
                .id(a.getId())
                .date(a.getDate())
                .heure(a.getHeure())
                .motif(a.getMotif())
                .statut(a.getStatut())
                .patientId(a.getPatientId())
                .medecinId(a.getMedecinId())
                .build();
    }
}
