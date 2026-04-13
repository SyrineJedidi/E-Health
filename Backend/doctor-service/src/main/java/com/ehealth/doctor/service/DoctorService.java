package com.ehealth.doctor.service;

import com.ehealth.doctor.dto.DoctorCreateRequest;
import com.ehealth.doctor.dto.DoctorDTO;
import com.ehealth.doctor.dto.DoctorUpdateRequest;
import com.ehealth.doctor.dto.PatientSummaryDTO;

import java.util.List;

public interface DoctorService {

    List<DoctorDTO> findAll();

    DoctorDTO findById(Long id);

    DoctorDTO create(DoctorCreateRequest request);

    DoctorDTO update(Long id, DoctorUpdateRequest request);

    void delete(Long id);

    List<PatientSummaryDTO> listPatients();

    List<DoctorDTO> searchDoctors(String keyword);

    List<DoctorDTO> getDoctorsBySpecialty(Long specialtyId);

    List<DoctorDTO> getDoctorsByDepartment(String department);
}
