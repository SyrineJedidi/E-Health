package com.ehealth.prescription.feign;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DoctorApiEnvelope {

    private boolean success;
    private DoctorFeignData data;
}
