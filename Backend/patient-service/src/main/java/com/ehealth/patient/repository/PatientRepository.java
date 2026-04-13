package com.ehealth.patient.repository;

import com.ehealth.patient.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByEmail(String email);

    Optional<Patient> findByPhone(String phone);

    List<Patient> findByLastNameContainingIgnoreCase(String lastName);

    boolean existsByEmail(String email);
}
