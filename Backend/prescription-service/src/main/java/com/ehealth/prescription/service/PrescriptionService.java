package com.ehealth.prescription.service;

import com.ehealth.prescription.dto.PrescriptionRequest;
import com.ehealth.prescription.dto.PrescriptionResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface PrescriptionService {

    PrescriptionResponse create(PrescriptionRequest request);

    PrescriptionResponse update(Long id, PrescriptionRequest request);

    PrescriptionResponse findById(Long id);

    List<PrescriptionResponse> findAll();

    void delete(Long id);

    List<PrescriptionResponse> findByPatientId(Long patientId);

    List<PrescriptionResponse> findByDoctorId(Long doctorId);

    PrescriptionResponse cancelPrescription(Long id);

    List<PrescriptionResponse> getActivePrescriptionsByPatient(Long patientId);

    List<PrescriptionResponse> getPrescriptionHistory(Long patientId, LocalDateTime from, LocalDateTime to);

    List<PrescriptionResponse> findForCurrentPatient();

    PrescriptionResponse updatePatientComment(Long prescriptionId, String comment);
}
