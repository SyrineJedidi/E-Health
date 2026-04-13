package com.ehealth.prescription.dto;

import com.ehealth.prescription.model.Prescription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionResponse {

    private Long id;

    private Long patientId;

    private Long doctorId;

    private LocalDateTime issuedAt;

    private LocalDate expirationDate;

    private String diagnosis;

    private String notes;

    private String patientComment;

    private Prescription.Status status;

    private List<MedicationDto> medications;
}
