package com.ehealth.prescription.service.impl;

import com.ehealth.prescription.dto.MedicationDto;
import com.ehealth.prescription.dto.PrescriptionRequest;
import com.ehealth.prescription.dto.PrescriptionResponse;
import com.ehealth.prescription.exception.PrescriptionNotFoundException;
import com.ehealth.prescription.exception.ResourceNotFoundException;
import com.ehealth.prescription.feign.DoctorApiEnvelope;
import com.ehealth.prescription.feign.DoctorFeignClient;
import com.ehealth.prescription.feign.PatientApiEnvelope;
import com.ehealth.prescription.feign.PatientFeignClient;
import com.ehealth.prescription.model.Medication;
import com.ehealth.prescription.model.MedicationForm;
import com.ehealth.prescription.model.Prescription;
import com.ehealth.prescription.model.PrescriptionItem;
import com.ehealth.prescription.repository.MedicationRepository;
import com.ehealth.prescription.repository.PrescriptionRepository;
import com.ehealth.prescription.service.PrescriptionService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final MedicationRepository medicationRepository;
    private final PatientFeignClient patientFeignClient;
    private final DoctorFeignClient doctorFeignClient;

    @Override
    @Transactional
    public PrescriptionResponse create(PrescriptionRequest request) {
        validatePatientAndDoctor(request.getPatientId(), request.getDoctorId());
        Prescription prescription = Prescription.builder()
                .patientId(request.getPatientId())
                .doctorId(request.getDoctorId())
                .status(request.getStatus() != null ? request.getStatus() : Prescription.Status.ACTIVE)
                .expirationDate(request.getExpirationDate())
                .diagnosis(request.getDiagnosis())
                .notes(request.getNotes())
                .build();
        for (MedicationDto dto : request.getMedications()) {
            prescription.addItem(toPrescriptionItem(dto));
        }
        return toResponse(prescriptionRepository.save(prescription));
    }

    @Override
    @Transactional
    public PrescriptionResponse update(Long id, PrescriptionRequest request) {
        Prescription prescription =
                prescriptionRepository
                        .findByIdWithItems(id)
                        .orElseThrow(() -> new PrescriptionNotFoundException("Ordonnance introuvable : " + id));
        validatePatientAndDoctor(request.getPatientId(), request.getDoctorId());
        prescription.setPatientId(request.getPatientId());
        prescription.setDoctorId(request.getDoctorId());
        if (request.getStatus() != null) {
            prescription.setStatus(request.getStatus());
        }
        prescription.setExpirationDate(request.getExpirationDate());
        prescription.setDiagnosis(request.getDiagnosis());
        prescription.setNotes(request.getNotes());
        prescription.clearItems();
        for (MedicationDto dto : request.getMedications()) {
            prescription.addItem(toPrescriptionItem(dto));
        }
        return toResponse(prescriptionRepository.save(prescription));
    }

    @Override
    @Transactional(readOnly = true)
    public PrescriptionResponse findById(Long id) {
        Prescription prescription =
                prescriptionRepository
                        .findByIdWithItems(id)
                        .orElseThrow(() -> new PrescriptionNotFoundException("Ordonnance introuvable : " + id));
        return toResponse(prescription);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionResponse> findAll() {
        return prescriptionRepository.findAllWithItems().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!prescriptionRepository.existsById(id)) {
            throw new PrescriptionNotFoundException("Ordonnance introuvable : " + id);
        }
        prescriptionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionResponse> findByPatientId(Long patientId) {
        return prescriptionRepository.findByPatientIdOrderByIssuedAtDesc(patientId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionResponse> findByDoctorId(Long doctorId) {
        return prescriptionRepository.findByDoctorIdOrderByIssuedAtDesc(doctorId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public PrescriptionResponse cancelPrescription(Long id) {
        Prescription p =
                prescriptionRepository
                        .findById(id)
                        .orElseThrow(() -> new PrescriptionNotFoundException("Ordonnance introuvable : " + id));
        p.setStatus(Prescription.Status.CANCELLED);
        return toResponse(prescriptionRepository.save(p));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionResponse> getActivePrescriptionsByPatient(Long patientId) {
        return prescriptionRepository
                .findByPatientIdAndStatusOrderByIssuedAtDesc(patientId, Prescription.Status.ACTIVE)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionResponse> getPrescriptionHistory(
            Long patientId, LocalDateTime from, LocalDateTime to) {
        return prescriptionRepository.findHistoryForPatient(patientId, from, to).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionResponse> findForCurrentPatient() {
        Long patientId = requireCurrentPatientId();
        return prescriptionRepository.findByPatientIdOrderByIssuedAtDesc(patientId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public PrescriptionResponse updatePatientComment(Long prescriptionId, String comment) {
        Long myPatientId = requireCurrentPatientId();
        Prescription p =
                prescriptionRepository
                        .findByIdWithItems(prescriptionId)
                        .orElseThrow(() -> new PrescriptionNotFoundException("Ordonnance introuvable : " + prescriptionId));
        if (!myPatientId.equals(p.getPatientId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cette ordonnance ne vous appartient pas");
        }
        String normalized = comment == null || comment.isBlank() ? null : comment.trim();
        p.setPatientComment(normalized);
        return toResponse(prescriptionRepository.save(p));
    }

    private Long requireCurrentPatientId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            PatientApiEnvelope env = patientFeignClient.lookupPatientByEmail(email);
            if (env != null
                    && env.isSuccess()
                    && env.getData() != null
                    && env.getData().getId() != null) {
                return env.getData().getId();
            }
        } catch (FeignException e) {
            if (e.status() == 403) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé");
            }
            log.warn("Feign lookup-email status={}", e.status());
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Fiche patient introuvable ou service patients indisponible.");
        }
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Aucune fiche patient pour l’email de ce compte. Utilisez le même email qu’à l’inscription"
                        + " patient.");
    }

    private void validatePatientAndDoctor(Long patientId, Long doctorId) {
        assertPatientExists(patientId);
        assertDoctorExists(doctorId);
    }

    private void assertPatientExists(Long id) {
        try {
            PatientApiEnvelope env = patientFeignClient.getPatient(id);
            if (env == null || !env.isSuccess() || env.getData() == null) {
                throw new IllegalArgumentException("Patient introuvable : " + id);
            }
        } catch (FeignException e) {
            log.warn("Feign patient-service id={} status={}", id, e.status());
            throw new IllegalArgumentException("Patient introuvable ou service patients indisponible : " + id);
        } catch (RuntimeException e) {
            log.warn("Erreur appel patient-service id={} : {}", id, e.getMessage());
            throw new IllegalArgumentException(
                    "Patient introuvable ou réponse du service patients invalide : " + id);
        }
    }

    private void assertDoctorExists(Long id) {
        try {
            DoctorApiEnvelope env = doctorFeignClient.getDoctor(id);
            if (env == null || !env.isSuccess() || env.getData() == null) {
                throw new IllegalArgumentException("Médecin introuvable : " + id);
            }
        } catch (FeignException e) {
            log.warn("Feign doctor-service id={} status={}", id, e.status());
            throw new IllegalArgumentException("Médecin introuvable ou service médecins indisponible : " + id);
        } catch (RuntimeException e) {
            log.warn("Erreur appel doctor-service id={} : {}", id, e.getMessage());
            throw new IllegalArgumentException(
                    "Médecin introuvable ou réponse du service médecins invalide : " + id);
        }
    }

    private Medication resolveMedication(MedicationDto dto) {
        if (dto.getMedicationId() != null) {
            return medicationRepository
                    .findById(dto.getMedicationId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Médicament introuvable : " + dto.getMedicationId()));
        }
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("Chaque ligne doit préciser medicationId ou name.");
        }
        String trimmed = dto.getName().trim();
        return medicationRepository
                .findByNameIgnoreCase(trimmed)
                .orElseGet(
                        () -> {
                            Medication created =
                                    Medication.builder()
                                            .name(trimmed)
                                            .dosage("")
                                            .form(MedicationForm.OTHER)
                                            .build();
                            try {
                                return medicationRepository.save(created);
                            } catch (DataIntegrityViolationException e) {
                                log.debug("Conflit catalogue médicament « {} », relecture", trimmed);
                                return medicationRepository
                                        .findByNameIgnoreCase(trimmed)
                                        .orElseThrow(() -> e);
                            }
                        });
    }

    private PrescriptionItem toPrescriptionItem(MedicationDto dto) {
        Medication med = resolveMedication(dto);
        return PrescriptionItem.builder()
                .medication(med)
                .dosage(dto.getDosage())
                .frequency(dto.getFrequency())
                .durationDays(dto.getDurationDays())
                .instructions(dto.getInstructions())
                .build();
    }

    private MedicationDto toLineDto(PrescriptionItem item) {
        Medication m = item.getMedication();
        if (m == null) {
            throw new IllegalStateException("Ligne d’ordonnance sans médicament associé (id ligne=" + item.getId() + ")");
        }
        return MedicationDto.builder()
                .id(item.getId())
                .medicationId(m.getId())
                .name(m.getName())
                .form(m.getForm())
                .dosage(item.getDosage())
                .frequency(item.getFrequency())
                .durationDays(item.getDurationDays())
                .instructions(item.getInstructions())
                .build();
    }

    private PrescriptionResponse toResponse(Prescription p) {
        List<PrescriptionItem> items = p.getItems() != null ? p.getItems() : Collections.emptyList();
        List<MedicationDto> lines = items.stream().map(this::toLineDto).toList();
        return PrescriptionResponse.builder()
                .id(p.getId())
                .patientId(p.getPatientId())
                .doctorId(p.getDoctorId())
                .issuedAt(p.getIssuedAt())
                .expirationDate(p.getExpirationDate())
                .diagnosis(p.getDiagnosis())
                .notes(p.getNotes())
                .patientComment(p.getPatientComment())
                .status(p.getStatus())
                .medications(lines)
                .build();
    }
}
