package com.ehealth.doctor.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDate;

/**
 * Miroir du {@code PatientDTO} patient-service pour désérialiser la réponse Feign.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PatientSummaryDTO {

    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String adresse;
    private LocalDate dateNaissance;
    private String groupeSanguin;
}
