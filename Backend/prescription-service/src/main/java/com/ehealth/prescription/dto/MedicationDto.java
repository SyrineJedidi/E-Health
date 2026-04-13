package com.ehealth.prescription.dto;

import com.ehealth.prescription.model.MedicationForm;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Ligne d’ordonnance (JSON {@code medications}) : soit {@code medicationId} (catalogue),
 * soit {@code name} pour résolution / création automatique (compat. ancien front).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicationDto {

    /** Identifiant de la ligne {@link com.ehealth.prescription.model.PrescriptionItem} (réponse). */
    private Long id;

    /** Référence au médicament du catalogue (requête). */
    private Long medicationId;

    /** Nom affiché / saisie libre si {@code medicationId} absent. */
    private String name;

    @NotBlank(message = "La posologie est obligatoire")
    private String dosage;

    @NotBlank(message = "La fréquence est obligatoire")
    private String frequency;

    private Integer durationDays;

    private String instructions;

    /** Réponse : forme galénique du catalogue. */
    private MedicationForm form;
}
