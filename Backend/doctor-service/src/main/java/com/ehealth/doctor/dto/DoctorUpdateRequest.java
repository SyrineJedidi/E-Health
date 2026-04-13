package com.ehealth.doctor.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

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
}
