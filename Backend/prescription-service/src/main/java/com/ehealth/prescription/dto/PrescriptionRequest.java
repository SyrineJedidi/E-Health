package com.ehealth.prescription.dto;

import com.ehealth.prescription.model.Prescription;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionRequest {

    @NotNull(message = "L'identifiant patient est obligatoire")
    private Long patientId;

    @NotNull(message = "L'identifiant médecin est obligatoire")
    private Long doctorId;

    private Prescription.Status status;

    private LocalDate expirationDate;

    private String diagnosis;

    private String notes;

    @NotEmpty(message = "Au moins un médicament est requis")
    @Valid
    private List<MedicationDto> medications;
}
