package com.ehealth.patient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DossierMedicalDTO {

    private PatientDTO patient;
    private List<RendezVousDTO> rendezVous;
    private String message;
}
