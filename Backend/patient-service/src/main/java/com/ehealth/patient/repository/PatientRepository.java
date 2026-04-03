package com.ehealth.patient.repository;

import com.ehealth.patient.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByEmail(String email);

    Optional<Patient> findByTelephone(String telephone);

    List<Patient> findByNomContainingIgnoreCase(String nom);

    boolean existsByEmail(String email);
}
