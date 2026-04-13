package com.ehealth.prescription.feign;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PatientApiEnvelope {

    private boolean success;
    private PatientFeignData data;
}
