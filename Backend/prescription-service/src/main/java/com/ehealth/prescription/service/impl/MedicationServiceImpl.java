package com.ehealth.prescription.service.impl;

import com.ehealth.prescription.dto.MedicationCatalogRequest;
import com.ehealth.prescription.dto.MedicationCatalogResponse;
import com.ehealth.prescription.exception.ResourceNotFoundException;
import com.ehealth.prescription.model.Medication;
import com.ehealth.prescription.model.MedicationForm;
import com.ehealth.prescription.repository.MedicationRepository;
import com.ehealth.prescription.service.MedicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicationServiceImpl implements MedicationService {

    private final MedicationRepository medicationRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MedicationCatalogResponse> findAll() {
        return medicationRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MedicationCatalogResponse findById(Long id) {
        return toResponse(
                medicationRepository
                        .findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Médicament introuvable : " + id)));
    }

    @Override
    @Transactional
    public MedicationCatalogResponse create(MedicationCatalogRequest request) {
        String name = request.getName().trim();
        if (medicationRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("Un médicament avec ce nom existe déjà : " + name);
        }
        MedicationForm form = request.getForm() != null ? request.getForm() : MedicationForm.OTHER;
        Medication saved =
                medicationRepository.save(
                        Medication.builder()
                                .name(name)
                                .dosage(request.getDosage())
                                .form(form)
                                .description(request.getDescription())
                                .build());
        log.debug("Médicament catalogue créé id={}", saved.getId());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public MedicationCatalogResponse update(Long id, MedicationCatalogRequest request) {
        Medication med =
                medicationRepository
                        .findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Médicament introuvable : " + id));
        String name = request.getName().trim();
        medicationRepository
                .findByNameIgnoreCase(name)
                .filter(other -> !other.getId().equals(id))
                .ifPresent(
                        x -> {
                            throw new IllegalArgumentException("Un médicament avec ce nom existe déjà : " + name);
                        });
        med.setName(name);
        med.setDosage(request.getDosage());
        if (request.getForm() != null) {
            med.setForm(request.getForm());
        }
        med.setDescription(request.getDescription());
        return toResponse(medicationRepository.save(med));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!medicationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Médicament introuvable : " + id);
        }
        medicationRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicationCatalogResponse> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return findAll();
        }
        return medicationRepository.findByNameContainingIgnoreCase(keyword.trim()).stream()
                .map(this::toResponse)
                .toList();
    }

    private MedicationCatalogResponse toResponse(Medication m) {
        return MedicationCatalogResponse.builder()
                .id(m.getId())
                .name(m.getName())
                .dosage(m.getDosage())
                .form(m.getForm())
                .description(m.getDescription())
                .build();
    }
}
