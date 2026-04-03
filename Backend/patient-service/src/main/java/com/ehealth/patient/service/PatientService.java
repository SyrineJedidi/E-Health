package com.ehealth.patient.service;

import com.ehealth.patient.dto.DossierMedicalDTO;
import com.ehealth.patient.dto.PatientDTO;

import java.util.List;

public interface PatientService {

    List<PatientDTO> getAllPatients();

    PatientDTO getPatientById(Long id);

    PatientDTO createPatient(PatientDTO dto);

    PatientDTO updatePatient(Long id, PatientDTO dto);

    void deletePatient(Long id);

    List<PatientDTO> searchByNom(String nom);

    List<PatientDTO> getPatientsByIds(List<Long> ids);

    DossierMedicalDTO getDossierMedical(Long id);
}
