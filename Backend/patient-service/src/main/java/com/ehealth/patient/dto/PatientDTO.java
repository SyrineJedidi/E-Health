package com.ehealth.patient.dto;

import com.ehealth.patient.model.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @NotBlank(groups = OnCreate.class, message = "Le téléphone est obligatoire")
    private String telephone;

    private String adresse;

    @NotNull(groups = OnCreate.class, message = "La date de naissance est obligatoire")
    @Past(groups = OnCreate.class, message = "La date de naissance doit être dans le passé")
    private LocalDate dateNaissance;

    private String groupeSanguin;

    private Gender gender;

    private String medicalHistory;

    private LocalDateTime createdAt;
}
