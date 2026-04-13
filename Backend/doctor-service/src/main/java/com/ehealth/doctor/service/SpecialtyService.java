package com.ehealth.doctor.service;

import com.ehealth.doctor.dto.SpecialtyDTO;
import com.ehealth.doctor.dto.SpecialtyRequest;

import java.util.List;

public interface SpecialtyService {

    List<SpecialtyDTO> findAll();

    SpecialtyDTO findById(Long id);

    SpecialtyDTO create(SpecialtyRequest request);

    SpecialtyDTO update(Long id, SpecialtyRequest request);

    void delete(Long id);
}
