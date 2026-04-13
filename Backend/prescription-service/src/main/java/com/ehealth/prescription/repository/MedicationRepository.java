package com.ehealth.prescription.repository;

import com.ehealth.prescription.model.Medication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedicationRepository extends JpaRepository<Medication, Long> {

    Optional<Medication> findByNameIgnoreCase(String name);

    List<Medication> findByNameContainingIgnoreCase(String namePart);

    boolean existsByNameIgnoreCase(String name);
}
