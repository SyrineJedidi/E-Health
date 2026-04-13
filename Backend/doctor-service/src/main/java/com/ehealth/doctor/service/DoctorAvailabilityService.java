package com.ehealth.doctor.service;

import com.ehealth.doctor.dto.AvailabilityDTO;
import com.ehealth.doctor.dto.AvailabilityRequest;

import java.util.List;

public interface DoctorAvailabilityService {

    List<AvailabilityDTO> listByDoctor(Long doctorId);

    AvailabilityDTO create(Long doctorId, AvailabilityRequest request);

    AvailabilityDTO update(Long doctorId, Long availabilityId, AvailabilityRequest request);

    void delete(Long doctorId, Long availabilityId);
}
