package com.ehealth.patient.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientDTO {

    /** Groupe Bean Validation pour la création (champs obligatoires). */
    public interface OnCreate {
    }

    private Long id;

    @NotBlank(groups = OnCreate.class, message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(groups = OnCreate.class, message = "Le prénom est obligatoire")
    private String prenom;

    @Email(message = "Email invalide")
    @NotBlank(groups = OnCreate.class, message = "L'email est obligatoire")
    private String email;

    private String telephone;

    private String adresse;

    private LocalDate dateNaissance;

    private String groupeSanguin;
}
