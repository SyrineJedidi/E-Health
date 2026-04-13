package com.ehealth.doctor.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrescriptionBriefJson {

    private Long id;
    private Long patientId;
    private Long doctorId;
}
