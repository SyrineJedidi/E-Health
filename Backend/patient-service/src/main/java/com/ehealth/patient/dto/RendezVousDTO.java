package com.ehealth.patient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RendezVousDTO {

    private Long id;
    private String date;
    private String heure;
    private String motif;
    private String statut;
    private Long patientId;
    private Long medecinId;
}
