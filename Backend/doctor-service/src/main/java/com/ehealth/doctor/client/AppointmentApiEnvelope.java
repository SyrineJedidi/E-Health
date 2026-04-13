package com.ehealth.doctor.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppointmentApiEnvelope {

    private boolean success;
    private List<RendezVousBriefJson> data;
}
