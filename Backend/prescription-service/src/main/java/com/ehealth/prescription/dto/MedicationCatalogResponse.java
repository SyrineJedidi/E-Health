package com.ehealth.prescription.dto;

import com.ehealth.prescription.model.MedicationForm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicationCatalogResponse {

    private Long id;
    private String name;
    private String dosage;
    private MedicationForm form;
    private String description;
}
