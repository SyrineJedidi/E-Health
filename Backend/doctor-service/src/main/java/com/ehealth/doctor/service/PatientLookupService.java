package com.ehealth.doctor.service;

import com.ehealth.doctor.dto.PatientSummaryDTO;

import java.util.List;

public interface PatientLookupService {

    List<PatientSummaryDTO> listPatientsViaPatientService();
}
