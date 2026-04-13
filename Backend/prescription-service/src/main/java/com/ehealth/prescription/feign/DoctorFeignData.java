package com.ehealth.prescription.feign;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DoctorFeignData {

    private Long id;
    private String nom;
    private String prenom;
    private String email;
}
