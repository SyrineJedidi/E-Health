package com.ehealth.doctor.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RendezVousBriefJson {

    private Long id;
    private String date;
    private String heure;
    private String motif;
    private String statut;
    private Long patientId;
    private Long medecinId;
}
