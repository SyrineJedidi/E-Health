package com.ehealth.doctor.service.impl;

import com.ehealth.doctor.client.AppointmentApiEnvelope;
import com.ehealth.doctor.client.AppointmentFeignClient;
import com.ehealth.doctor.client.PrescriptionBriefJson;
import com.ehealth.doctor.client.PrescriptionServiceClient;
import com.ehealth.doctor.client.RendezVousBriefJson;
import com.ehealth.doctor.dto.DoctorCreateRequest;
import com.ehealth.doctor.dto.DoctorDTO;
import com.ehealth.doctor.dto.PatientSummaryDTO;
import com.ehealth.doctor.dto.RendezVousViewDTO;
import com.ehealth.doctor.model.Doctor;
import com.ehealth.doctor.client.PatientListResponse;
import com.ehealth.doctor.client.PatientServiceClient;
import com.ehealth.doctor.repository.DoctorRepository;
import com.ehealth.doctor.security.JwtUtil;
import com.ehealth.doctor.service.DoctorPortalService;
import com.ehealth.doctor.service.DoctorService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;

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
    private final JwtUtil jwtUtil;

    @Value("${ehealth.doctor.portal.auto-provision-doctor:true}")
    private boolean autoProvisionDoctor;

    @Value("${ehealth.doctor.portal.default-specialty-id:2}")
    private long defaultSpecialtyId;

    @Override
    @Transactional
    public DoctorDTO getCurrentDoctor() {
        Doctor doc = requireCurrentDoctor();
        return doctorService.findById(doc.getId());
    }

    @Override
    @Transactional
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
    @Transactional
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
        String normalized = email.trim();
        return doctorRepository
                .findByEmailIgnoreCase(normalized)
                .orElseGet(() -> tryAutoProvisionOrThrow(normalized));
    }

    private Doctor tryAutoProvisionOrThrow(String normalizedEmail) {
        if (!autoProvisionDoctor) {
            throw portalNotFound(normalizedEmail);
        }
        String token = bearerTokenFromRequest();
        String role = token != null ? jwtUtil.extractRole(token) : null;
        if (role == null || !"DOCTOR".equalsIgnoreCase(role.trim())) {
            throw portalNotFound(normalizedEmail);
        }
        DoctorCreateRequest req = bootstrapRequest(normalizedEmail);
        doctorService.create(req);
        return doctorRepository
                .findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Fiche médecin non créée."));
    }

    private static ResponseStatusException portalNotFound(String normalizedEmail) {
        return new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Aucune fiche médecin pour ce compte. Utilisez le même email que dans l’annuaire (GET /api/doctors),"
                        + " enregistrez-vous en tant que DOCTOR avec cet email, ou laissez"
                        + " ehealth.doctor.portal.auto-provision-doctor=true pour une fiche auto si le JWT indique DOCTOR."
                        + " (email JWT : « "
                        + normalizedEmail
                        + " »)");
    }

    private DoctorCreateRequest bootstrapRequest(String email) {
        DoctorCreateRequest r = new DoctorCreateRequest();
        r.setEmail(email);
        r.setSpecialtyId(defaultSpecialtyId);
        int at = email.indexOf('@');
        String local = at > 0 ? email.substring(0, at) : email;
        String[] parts = local.split("[._\\-]+");
        r.setPrenom(capitalizeWord(parts.length > 0 ? parts[0] : "Médecin"));
        r.setNom(capitalizeWord(parts.length > 1 ? parts[1] : "Portail"));
        r.setDepartment("À compléter");
        return r;
    }

    private static String capitalizeWord(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
    }

    private static String bearerTokenFromRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return null;
        }
        HttpServletRequest request = attrs.getRequest();
        String h = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (h != null && h.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return h.substring(7).trim();
        }
        return null;
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
