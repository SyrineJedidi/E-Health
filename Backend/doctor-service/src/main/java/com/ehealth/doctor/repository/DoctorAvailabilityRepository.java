package com.ehealth.doctor.repository;

import com.ehealth.doctor.model.DoctorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Long> {

    List<DoctorAvailability> findByDoctor_IdOrderByDayOfWeekAscHeureDebutAsc(Long doctorId);
}
