package com.ehealth.doctor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDTO {

    private Long id;
    private String nom;
    private String prenom;
    private String email;
    /** Identifiant de la spécialité (référence métier). */
    private Long specialtyId;
    /** Libellé de la spécialité (affichage). */
    private String specialite;
    private String telephone;
    private String service;
    private String registrationNumber;
    private String department;
    private boolean active;
    private java.time.LocalDateTime createdAt;
}
