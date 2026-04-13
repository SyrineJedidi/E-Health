package com.ehealth.doctor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityDTO {

    private Long id;
    private Long doctorId;
    private DayOfWeek dayOfWeek;
    private LocalTime heureDebut;
    private LocalTime heureFin;
}
