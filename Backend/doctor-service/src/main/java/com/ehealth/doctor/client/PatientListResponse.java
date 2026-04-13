package com.ehealth.doctor.client;

import com.ehealth.doctor.dto.PatientSummaryDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PatientListResponse {

    private boolean success;
    private String message;
    private List<PatientSummaryDTO> data;
}
