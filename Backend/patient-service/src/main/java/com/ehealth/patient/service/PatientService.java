package com.ehealth.patient.service;

import com.ehealth.patient.dto.DossierMedicalDTO;
import com.ehealth.patient.dto.PatientDTO;

import java.util.List;

public interface PatientService {

    List<PatientDTO> getAllPatients();

    PatientDTO getPatientById(Long id);

    PatientDTO getPatientByEmail(String email);

    PatientDTO createPatient(PatientDTO dto);

    PatientDTO updatePatient(Long id, PatientDTO dto, String profileChangeInitiator);

    void deletePatient(Long id);

    List<PatientDTO> searchByNom(String nom);

    /** Recherche par mot-clé (nom de famille, insensible à la casse). */
    List<PatientDTO> searchPatients(String keyword);

    List<PatientDTO> getPatientsByIds(List<Long> ids);

    DossierMedicalDTO getDossierMedical(Long id);
}
