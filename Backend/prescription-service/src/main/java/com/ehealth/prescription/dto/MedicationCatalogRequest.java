package com.ehealth.prescription.dto;

import com.ehealth.prescription.model.MedicationForm;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicationCatalogRequest {

    @NotBlank(message = "Le nom est obligatoire")
    private String name;

    private String dosage;

    private MedicationForm form;

    private String description;
}
