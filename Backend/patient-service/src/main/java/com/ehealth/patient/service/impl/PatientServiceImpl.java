package com.ehealth.patient.service.impl;

import com.ehealth.patient.client.AppointmentClient;
import com.ehealth.patient.dto.DossierMedicalDTO;
import com.ehealth.patient.dto.PatientDTO;
import com.ehealth.patient.dto.RendezVousDTO;
import com.ehealth.patient.exception.EmailAlreadyExistsException;
import com.ehealth.patient.exception.PatientNotFoundException;
import com.ehealth.patient.model.Patient;
import com.ehealth.patient.repository.PatientRepository;
import com.ehealth.patient.service.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentClient appointmentClient;

    /**
     * Retourne la liste de tous les patients en base.
     */
    @Override
    @Transactional(readOnly = true)
    public List<PatientDTO> getAllPatients() {
        log.info("Récupération de tous les patients");
        return patientRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Retourne un patient par identifiant ou lève une exception si absent.
     */
    @Override
    @Transactional(readOnly = true)
    public PatientDTO getPatientById(Long id) {
        log.info("Récupération du patient id={}", id);
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(id));
        return toDto(patient);
    }

    /**
     * Crée un patient après vérification d'unicité de l'email.
     */
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

    /**
     * Met à jour un patient en ne modifiant que les champs non nuls du DTO.
     */
    @Override
    public PatientDTO updatePatient(Long id, PatientDTO dto) {
        log.info("Mise à jour du patient id={}", id);
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(id));
        if (dto.getNom() != null) {
            patient.setNom(dto.getNom());
        }
        if (dto.getPrenom() != null) {
            patient.setPrenom(dto.getPrenom());
        }
        if (dto.getEmail() != null) {
            if (!dto.getEmail().equals(patient.getEmail())
                    && patientRepository.existsByEmail(dto.getEmail())) {
                throw new EmailAlreadyExistsException(dto.getEmail());
            }
            patient.setEmail(dto.getEmail());
        }
        if (dto.getTelephone() != null) {
            patient.setTelephone(dto.getTelephone());
        }
        if (dto.getAdresse() != null) {
            patient.setAdresse(dto.getAdresse());
        }
        if (dto.getDateNaissance() != null) {
            patient.setDateNaissance(dto.getDateNaissance());
        }
        if (dto.getGroupeSanguin() != null) {
            patient.setGroupeSanguin(dto.getGroupeSanguin());
        }
        return toDto(patientRepository.save(patient));
    }

    /**
     * Supprime un patient par identifiant.
     */
    @Override
    public void deletePatient(Long id) {
        log.info("Suppression du patient id={}", id);
        if (!patientRepository.existsById(id)) {
            throw new PatientNotFoundException(id);
        }
        patientRepository.deleteById(id);
    }

    /**
     * Recherche des patients dont le nom contient la chaîne donnée (insensible à la casse).
     */
    @Override
    @Transactional(readOnly = true)
    public List<PatientDTO> searchByNom(String nom) {
        log.info("Recherche de patients par nom contenant '{}'", nom);
        return patientRepository.findByNomContainingIgnoreCase(nom).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Retourne les patients correspondant aux identifiants fournis (ordre non garanti identique à la liste d'entrée).
     */
    @Override
    @Transactional(readOnly = true)
    public List<PatientDTO> getPatientsByIds(List<Long> ids) {
        log.info("Récupération batch pour {} identifiants", ids != null ? ids.size() : 0);
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return patientRepository.findAllById(ids).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Construit le dossier médical : patient et liste des rendez-vous via appointment-service.
     */
    @Override
    @Transactional(readOnly = true)
    public DossierMedicalDTO getDossierMedical(Long id) {
        log.info("Constitution du dossier médical pour le patient id={}", id);
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(id));
        PatientDTO patientDto = toDto(patient);
        List<RendezVousDTO> rendezVous = appointmentClient.getRendezVousByPatientId(id);
        String message = rendezVous.isEmpty()
                ? "Rendez-vous indisponibles"
                : "Dossier complet";
        return DossierMedicalDTO.builder()
                .patient(patientDto)
                .rendezVous(rendezVous)
                .message(message)
                .build();
    }

    private PatientDTO toDto(Patient p) {
        return PatientDTO.builder()
                .id(p.getId())
                .nom(p.getNom())
                .prenom(p.getPrenom())
                .email(p.getEmail())
                .telephone(p.getTelephone())
                .adresse(p.getAdresse())
                .dateNaissance(p.getDateNaissance())
                .groupeSanguin(p.getGroupeSanguin())
                .build();
    }

    private Patient fromDto(PatientDTO dto) {
        Patient p = new Patient();
        p.setNom(dto.getNom());
        p.setPrenom(dto.getPrenom());
        p.setEmail(dto.getEmail());
        p.setTelephone(dto.getTelephone());
        p.setAdresse(dto.getAdresse());
        p.setDateNaissance(dto.getDateNaissance());
        p.setGroupeSanguin(dto.getGroupeSanguin());
        return p;
    }
}
