package com.ehealth.prescription.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientCommentRequest {

    /** Texte pour le médecin ; vide ou null efface le commentaire. */
    @Size(max = 2000, message = "Le commentaire ne peut pas dépasser 2000 caractères")
    private String comment;
}
