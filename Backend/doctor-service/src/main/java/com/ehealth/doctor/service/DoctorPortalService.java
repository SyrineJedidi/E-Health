package com.ehealth.doctor.service;

import com.ehealth.doctor.dto.DoctorDTO;
import com.ehealth.doctor.dto.PatientSummaryDTO;
import com.ehealth.doctor.dto.RendezVousViewDTO;

import java.util.List;

public interface DoctorPortalService {

    DoctorDTO getCurrentDoctor();

    List<PatientSummaryDTO> getMyPatients();

    List<RendezVousViewDTO> getMyAppointments();
}
