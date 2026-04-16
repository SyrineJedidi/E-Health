package com.ehealth.doctor.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class DoctorCreateRequest {

    @NotBlank
    private String nom;

    @NotBlank
    private String prenom;

    @Email
    @NotBlank
    private String email;

    @NotNull
    private Long specialtyId;

    private String telephone;

    private String service;

    /** Numéro d’ordre / RPPS ; généré automatiquement si absent. */
    private String registrationNumber;

    private String department;

    /** Créneaux rattachés au médecin (même payload que l’ancienne API disponibilités). */
    private List<@Valid AvailabilityRequest> availabilities;
}
