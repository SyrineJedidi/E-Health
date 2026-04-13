package com.ehealth.doctor.repository;

import com.ehealth.doctor.model.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {

    boolean existsByCodeIgnoreCase(String code);

    Optional<Specialty> findByCodeIgnoreCase(String code);

}
