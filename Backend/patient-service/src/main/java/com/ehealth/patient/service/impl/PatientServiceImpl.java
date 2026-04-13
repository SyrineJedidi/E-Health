package com.ehealth.patient.service.impl;

import com.ehealth.patient.client.AppointmentClient;
import com.ehealth.patient.dto.DossierMedicalDTO;
import com.ehealth.patient.dto.PatientDTO;
import com.ehealth.patient.dto.RendezVousDTO;
import com.ehealth.patient.exception.EmailAlreadyExistsException;
import com.ehealth.patient.exception.PatientNotFoundException;
import com.ehealth.patient.exception.ResourceNotFoundException;
import com.ehealth.patient.model.Patient;
import com.ehealth.patient.repository.PatientRepository;
import com.ehealth.patient.service.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentClient appointmentClient;

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
        return toDto(saved);
    }

    @Override
    public PatientDTO updatePatient(Long id, PatientDTO dto) {
        log.info("Mise à jour du patient id={}", id);
        Patient patient = patientRepository
                .findById(id)
                .orElseThrow(() -> new PatientNotFoundException(id));
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
        return toDto(patientRepository.save(patient));
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
