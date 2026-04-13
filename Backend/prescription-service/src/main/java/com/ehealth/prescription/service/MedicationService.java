package com.ehealth.prescription.service;

import com.ehealth.prescription.dto.MedicationCatalogRequest;
import com.ehealth.prescription.dto.MedicationCatalogResponse;

import java.util.List;

public interface MedicationService {

    List<MedicationCatalogResponse> findAll();

    MedicationCatalogResponse findById(Long id);

    MedicationCatalogResponse create(MedicationCatalogRequest request);

    MedicationCatalogResponse update(Long id, MedicationCatalogRequest request);

    void delete(Long id);

    List<MedicationCatalogResponse> search(String keyword);
}
