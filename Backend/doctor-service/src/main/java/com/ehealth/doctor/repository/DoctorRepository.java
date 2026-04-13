package com.ehealth.doctor.repository;

import com.ehealth.doctor.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    boolean existsByRegistrationNumberIgnoreCase(String registrationNumber);

    boolean existsByRegistrationNumberIgnoreCaseAndIdNot(String registrationNumber, Long id);

    long countBySpecialty_Id(Long specialtyId);

    List<Doctor> findByNomContainingIgnoreCase(String keyword);

    @Query(
            "select distinct d from Doctor d join fetch d.specialty where lower(d.nom) like"
                    + " lower(concat('%', :kw, '%')) order by d.nom, d.prenom")
    List<Doctor> searchByNomContainingWithSpecialty(@Param("kw") String keyword);

    @Query("select distinct d from Doctor d join fetch d.specialty where d.specialty.id = :sid order by d.nom, d.prenom")
    List<Doctor> findBySpecialtyIdWithSpecialty(@Param("sid") Long specialtyId);

    @Query("select distinct d from Doctor d join fetch d.specialty where lower(d.department) = lower(:dept) order by d.nom, d.prenom")
    List<Doctor> findByDepartmentWithSpecialty(@Param("dept") String department);

    @Query("select distinct d from Doctor d join fetch d.specialty where d.active = :active order by d.nom, d.prenom")
    List<Doctor> findByActiveWithSpecialty(@Param("active") boolean active);

    @Query("select distinct d from Doctor d join fetch d.specialty order by d.nom, d.prenom")
    List<Doctor> findAllWithSpecialty();

    @Query("select distinct d from Doctor d join fetch d.specialty where d.id = :id")
    Optional<Doctor> findByIdWithSpecialty(@Param("id") Long id);

    Optional<Doctor> findByEmailIgnoreCase(String email);
}
