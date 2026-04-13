package com.ehealth.prescription.repository;

import com.ehealth.prescription.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    @Query(
            "select distinct p from Prescription p left join fetch p.items i left join fetch i.medication"
                    + " where p.patientId = :pid order by p.issuedAt desc")
    List<Prescription> findByPatientIdOrderByIssuedAtDesc(@Param("pid") Long patientId);

    @Query(
            "select distinct p from Prescription p left join fetch p.items i left join fetch i.medication"
                    + " where p.doctorId = :did order by p.issuedAt desc")
    List<Prescription> findByDoctorIdOrderByIssuedAtDesc(@Param("did") Long doctorId);

    List<Prescription> findByStatus(Prescription.Status status);

    @Query(
            "select distinct p from Prescription p left join fetch p.items i left join fetch i.medication"
                    + " where p.patientId = :pid and p.status = :st order by p.issuedAt desc")
    List<Prescription> findByPatientIdAndStatusOrderByIssuedAtDesc(
            @Param("pid") Long patientId, @Param("st") Prescription.Status status);

    List<Prescription> findByIssuedAtBetween(LocalDateTime from, LocalDateTime to);

    @Query(
            "select distinct p from Prescription p left join fetch p.items i left join fetch i.medication"
                    + " where p.patientId = :pid and p.issuedAt between :from and :to order by p.issuedAt desc")
    List<Prescription> findHistoryForPatient(
            @Param("pid") Long patientId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    @Query(
            "select distinct p from Prescription p left join fetch p.items i left join fetch i.medication"
                    + " where p.id = :id")
    Optional<Prescription> findByIdWithItems(@Param("id") Long id);

    @Query(
            "select distinct p from Prescription p left join fetch p.items i left join fetch i.medication"
                    + " order by p.issuedAt desc")
    List<Prescription> findAllWithItems();
}
