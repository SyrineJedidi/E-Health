package com.ehealth.doctor.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
public class AvailabilityRequest {

    @NotNull
    private DayOfWeek dayOfWeek;

    @NotNull
    private LocalTime heureDebut;

    @NotNull
    private LocalTime heureFin;
}
