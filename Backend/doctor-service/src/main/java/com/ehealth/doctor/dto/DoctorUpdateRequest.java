package com.ehealth.doctor.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.Data;

import java.util.List;

@Data
public class DoctorUpdateRequest {

    private String nom;
    private String prenom;

    @Email
    private String email;

    private Long specialtyId;
    private String telephone;
    private String service;
    private String registrationNumber;
    private String department;
    private Boolean active;

    /**
     * Si présent (y compris liste vide), remplace toutes les disponibilités du médecin.
     * {@code null} = ne pas modifier les créneaux existants.
     */
    private List<@Valid AvailabilityRequest> availabilities;
}
