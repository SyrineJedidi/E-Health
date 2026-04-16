package com.ehealth.patient.service.impl;

import com.ehealth.patient.client.AppointmentClient;
import com.ehealth.patient.dto.DossierMedicalDTO;
import com.ehealth.patient.dto.PatientDTO;
import com.ehealth.patient.dto.RendezVousDTO;
import com.ehealth.patient.exception.EmailAlreadyExistsException;
import com.ehealth.patient.exception.PatientNotFoundException;
import com.ehealth.patient.exception.ResourceNotFoundException;
import com.ehealth.patient.messaging.PatientProfileUpdatedEvent;
import com.ehealth.patient.messaging.PatientProfileUpdatedEventPublisher;
import com.ehealth.patient.messaging.PatientRegisteredEvent;
import com.ehealth.patient.messaging.PatientRegisteredEventPublisher;
import com.ehealth.patient.model.Gender;
import com.ehealth.patient.model.Patient;
import com.ehealth.patient.repository.PatientRepository;
import com.ehealth.patient.service.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentClient appointmentClient;
    private final PatientRegisteredEventPublisher patientRegisteredEventPublisher;
    private final PatientProfileUpdatedEventPublisher patientProfileUpdatedEventPublisher;

    @Override
    @Transactional(readOnly = true)
    public List<PatientDTO> getAllPatients() {
        log.info("Récupération de tous les patients");
        return patientRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PatientDTO getPatientById(Long id) {
        log.info("Récupération du patient id={}", id);
        Patient patient = patientRepository
                .findById(id)
                .orElseThrow(() -> new PatientNotFoundException(id));
        return toDto(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientDTO getPatientByEmail(String email) {
        log.info("Récupération du patient email={}", email);
        Patient patient = patientRepository
                .findByEmail(email.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with email: " + email));
        return toDto(patient);
    }

    @Override
    public PatientDTO createPatient(PatientDTO dto) {
        log.info("Création d'un patient avec l'email {}", dto.getEmail());
        if (patientRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException(dto.getEmail());
        }
        Patient entity = fromDto(dto);
        Patient saved = patientRepository.save(entity);
        patientRegisteredEventPublisher.publish(
                new PatientRegisteredEvent(
                        saved.getId(),
                        saved.getEmail(),
                        saved.getFirstName(),
                        saved.getLastName(),
                        Instant.now()));
        return toDto(saved);
    }

    @Override
    public PatientDTO updatePatient(Long id, PatientDTO dto, String profileChangeInitiator) {
        log.info("Mise à jour du patient id={}", id);
        Patient patient = patientRepository
                .findById(id)
                .orElseThrow(() -> new PatientNotFoundException(id));
        String prevNom = patient.getFirstName();
        String prevPrenom = patient.getLastName();
        String prevEmail = patient.getEmail();
        String prevPhone = patient.getPhone();
        String prevAddr = patient.getAddress();
        String prevBlood = patient.getBloodType();
        String prevHistory = patient.getMedicalHistory();
        LocalDate prevDob = patient.getDateOfBirth();
        Gender prevGender = patient.getGender();

        if (dto.getNom() != null) {
            patient.setFirstName(dto.getNom());
        }
        if (dto.getPrenom() != null) {
            patient.setLastName(dto.getPrenom());
        }
        if (dto.getEmail() != null) {
            if (!dto.getEmail().equals(patient.getEmail()) && patientRepository.existsByEmail(dto.getEmail())) {
                throw new EmailAlreadyExistsException(dto.getEmail());
            }
            patient.setEmail(dto.getEmail());
        }
        if (dto.getTelephone() != null) {
            patient.setPhone(dto.getTelephone());
        }
        if (dto.getAdresse() != null) {
            patient.setAddress(dto.getAdresse());
        }
        if (dto.getDateNaissance() != null) {
            patient.setDateOfBirth(dto.getDateNaissance());
        }
        if (dto.getGroupeSanguin() != null) {
            patient.setBloodType(dto.getGroupeSanguin());
        }
        if (dto.getGender() != null) {
            patient.setGender(dto.getGender());
        }
        if (dto.getMedicalHistory() != null) {
            patient.setMedicalHistory(dto.getMedicalHistory());
        }
        Patient saved = patientRepository.save(patient);

        List<String> changed = new ArrayList<>();
        if (dto.getNom() != null && !Objects.equals(prevNom, saved.getFirstName())) {
            changed.add("nom");
        }
        if (dto.getPrenom() != null && !Objects.equals(prevPrenom, saved.getLastName())) {
            changed.add("prenom");
        }
        if (dto.getEmail() != null && !Objects.equals(prevEmail, saved.getEmail())) {
            changed.add("email");
        }
        if (dto.getTelephone() != null && !Objects.equals(prevPhone, saved.getPhone())) {
            changed.add("telephone");
        }
        if (dto.getAdresse() != null && !Objects.equals(prevAddr, saved.getAddress())) {
            changed.add("adresse");
        }
        if (dto.getDateNaissance() != null && !Objects.equals(prevDob, saved.getDateOfBirth())) {
            changed.add("dateNaissance");
        }
        if (dto.getGroupeSanguin() != null && !Objects.equals(prevBlood, saved.getBloodType())) {
            changed.add("groupeSanguin");
        }
        if (dto.getGender() != null && !Objects.equals(prevGender, saved.getGender())) {
            changed.add("sexe");
        }
        if (dto.getMedicalHistory() != null && !Objects.equals(prevHistory, saved.getMedicalHistory())) {
            changed.add("antecedents");
        }

        // Toujours publier après un PUT réussi : si le corps JSON reprend les mêmes valeurs qu’en base,
        // « changed » est vide — avant on n’envoyait rien et l’audit doctor restait vide.
        List<String> changedForEvent =
                changed.isEmpty() ? List.of("aucune_modification_detectee") : changed;
        String alert =
                changed.stream().anyMatch(a -> a.equals("antecedents") || a.equals("groupeSanguin"))
                        ? "VIGILANCE_CLINIQUE"
                        : "MISE_A_JOUR_ROUTINE";
        String initiator =
                profileChangeInitiator != null && !profileChangeInitiator.isBlank()
                        ? profileChangeInitiator.trim()
                        : "UNKNOWN";
        patientProfileUpdatedEventPublisher.publish(
                new PatientProfileUpdatedEvent(
                        saved.getId(),
                        saved.getEmail(),
                        saved.getFirstName(),
                        saved.getLastName(),
                        "PROFILE_UPDATED",
                        changedForEvent,
                        alert,
                        Instant.now(),
                        "patient-service",
                        initiator,
                        UUID.randomUUID().toString()));

        return toDto(saved);
    }

    @Override
    public void deletePatient(Long id) {
        log.info("Suppression du patient id={}", id);
        if (!patientRepository.existsById(id)) {
            throw new PatientNotFoundException(id);
        }
        patientRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientDTO> searchByNom(String nom) {
        return searchPatients(nom);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientDTO> searchPatients(String keyword) {
        log.info("Recherche patients mot-clé '{}'", keyword);
        return patientRepository.findByLastNameContainingIgnoreCase(keyword).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientDTO> getPatientsByIds(List<Long> ids) {
        log.info("Récupération batch pour {} identifiants", ids != null ? ids.size() : 0);
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return patientRepository.findAllById(ids).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DossierMedicalDTO getDossierMedical(Long id) {
        log.info("Constitution du dossier médical pour le patient id={}", id);
        Patient patient = patientRepository
                .findById(id)
                .orElseThrow(() -> new PatientNotFoundException(id));
        PatientDTO patientDto = toDto(patient);
        List<RendezVousDTO> rendezVous;
        try {
            List<RendezVousDTO> raw = appointmentClient.getRendezVousByPatientId(id);
            rendezVous = raw != null ? raw : Collections.emptyList();
        } catch (Exception ex) {
            log.warn(
                    "Impossible de récupérer les rendez-vous pour le patient id={} (service absent, JSON"
                            + " inattendu ou erreur réseau) : {}",
                    id,
                    ex.getMessage());
            rendezVous = Collections.emptyList();
        }
        String message = rendezVous.isEmpty() ? "Rendez-vous indisponibles" : "Dossier complet";
        return DossierMedicalDTO.builder()
                .patient(patientDto)
                .rendezVous(rendezVous)
                .message(message)
                .build();
    }

    private PatientDTO toDto(Patient p) {
        return PatientDTO.builder()
                .id(p.getId())
                .nom(p.getFirstName())
                .prenom(p.getLastName())
                .email(p.getEmail())
                .telephone(p.getPhone())
                .adresse(p.getAddress())
                .dateNaissance(p.getDateOfBirth())
                .groupeSanguin(p.getBloodType())
                .gender(p.getGender())
                .medicalHistory(p.getMedicalHistory())
                .createdAt(p.getCreatedAt())
                .build();
    }

    private Patient fromDto(PatientDTO dto) {
        Patient p = new Patient();
        p.setFirstName(dto.getNom());
        p.setLastName(dto.getPrenom());
        p.setEmail(dto.getEmail());
        p.setPhone(dto.getTelephone());
        p.setAddress(dto.getAdresse());
        p.setDateOfBirth(dto.getDateNaissance());
        p.setBloodType(dto.getGroupeSanguin());
        p.setGender(dto.getGender());
        p.setMedicalHistory(dto.getMedicalHistory());
        return p;
    }
}
