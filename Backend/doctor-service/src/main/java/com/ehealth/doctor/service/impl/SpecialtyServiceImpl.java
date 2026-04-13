package com.ehealth.doctor.service.impl;

import com.ehealth.doctor.dto.SpecialtyDTO;
import com.ehealth.doctor.dto.SpecialtyRequest;
import com.ehealth.doctor.exception.DuplicateSpecialtyCodeException;
import com.ehealth.doctor.exception.SpecialtyInUseException;
import com.ehealth.doctor.exception.SpecialtyNotFoundException;
import com.ehealth.doctor.model.Specialty;
import com.ehealth.doctor.repository.DoctorRepository;
import com.ehealth.doctor.repository.SpecialtyRepository;
import com.ehealth.doctor.service.SpecialtyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecialtyServiceImpl implements SpecialtyService {

    private final SpecialtyRepository specialtyRepository;
    private final DoctorRepository doctorRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SpecialtyDTO> findAll() {
        return specialtyRepository.findAll().stream()
                .sorted((a, b) -> a.getLabel().compareToIgnoreCase(b.getLabel()))
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SpecialtyDTO findById(Long id) {
        Specialty s = specialtyRepository.findById(id)
                .orElseThrow(() -> new SpecialtyNotFoundException("Spécialité introuvable : " + id));
        return toDto(s);
    }

    @Override
    @Transactional
    public SpecialtyDTO create(SpecialtyRequest request) {
        String code = request.getCode().trim();
        if (specialtyRepository.existsByCodeIgnoreCase(code)) {
            throw new DuplicateSpecialtyCodeException("Code spécialité déjà utilisé : " + code);
        }
        Specialty s = Specialty.builder()
                .code(code)
                .label(request.getLabel().trim())
                .description(trimToNull(request.getDescription()))
                .build();
        return toDto(specialtyRepository.save(s));
    }

    @Override
    @Transactional
    public SpecialtyDTO update(Long id, SpecialtyRequest request) {
        Specialty s = specialtyRepository.findById(id)
                .orElseThrow(() -> new SpecialtyNotFoundException("Spécialité introuvable : " + id));
        String code = request.getCode().trim();
        specialtyRepository.findByCodeIgnoreCase(code).ifPresent(other -> {
            if (!other.getId().equals(id)) {
                throw new DuplicateSpecialtyCodeException("Code spécialité déjà utilisé : " + code);
            }
        });
        s.setCode(code);
        s.setLabel(request.getLabel().trim());
        s.setDescription(trimToNull(request.getDescription()));
        return toDto(specialtyRepository.save(s));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!specialtyRepository.existsById(id)) {
            throw new SpecialtyNotFoundException("Spécialité introuvable : " + id);
        }
        if (doctorRepository.countBySpecialty_Id(id) > 0) {
            throw new SpecialtyInUseException("Impossible de supprimer : des médecins utilisent cette spécialité.");
        }
        specialtyRepository.deleteById(id);
    }

    private static String trimToNull(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private SpecialtyDTO toDto(Specialty s) {
        return SpecialtyDTO.builder()
                .id(s.getId())
                .code(s.getCode())
                .label(s.getLabel())
                .description(s.getDescription())
                .build();
    }
}
