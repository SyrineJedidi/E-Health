package com.ehealth.doctor.service.impl;

import com.ehealth.doctor.dto.AvailabilityDTO;
import com.ehealth.doctor.dto.AvailabilityRequest;
import com.ehealth.doctor.exception.DoctorAvailabilityNotFoundException;
import com.ehealth.doctor.exception.DoctorNotFoundException;
import com.ehealth.doctor.exception.InvalidAvailabilityException;
import com.ehealth.doctor.model.Doctor;
import com.ehealth.doctor.model.DoctorAvailability;
import com.ehealth.doctor.repository.DoctorAvailabilityRepository;
import com.ehealth.doctor.repository.DoctorRepository;
import com.ehealth.doctor.service.DoctorAvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorAvailabilityServiceImpl implements DoctorAvailabilityService {

    private final DoctorAvailabilityRepository availabilityRepository;
    private final DoctorRepository doctorRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AvailabilityDTO> listByDoctor(Long doctorId) {
        ensureDoctor(doctorId);
        return availabilityRepository.findByDoctor_IdOrderByDayOfWeekAscHeureDebutAsc(doctorId).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional
    public AvailabilityDTO create(Long doctorId, AvailabilityRequest request) {
        Doctor doctor = ensureDoctor(doctorId);
        validateSlot(request);
        DoctorAvailability slot = DoctorAvailability.builder()
                .doctor(doctor)
                .dayOfWeek(request.getDayOfWeek())
                .heureDebut(request.getHeureDebut())
                .heureFin(request.getHeureFin())
                .build();
        return toDto(availabilityRepository.save(slot));
    }

    @Override
    @Transactional
    public AvailabilityDTO update(Long doctorId, Long availabilityId, AvailabilityRequest request) {
        ensureDoctor(doctorId);
        validateSlot(request);
        DoctorAvailability slot = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new DoctorAvailabilityNotFoundException("Créneau introuvable : " + availabilityId));
        if (!slot.getDoctor().getId().equals(doctorId)) {
            throw new DoctorAvailabilityNotFoundException("Créneau introuvable pour ce médecin.");
        }
        slot.setDayOfWeek(request.getDayOfWeek());
        slot.setHeureDebut(request.getHeureDebut());
        slot.setHeureFin(request.getHeureFin());
        return toDto(availabilityRepository.save(slot));
    }

    @Override
    @Transactional
    public void delete(Long doctorId, Long availabilityId) {
        ensureDoctor(doctorId);
        DoctorAvailability slot = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new DoctorAvailabilityNotFoundException("Créneau introuvable : " + availabilityId));
        if (!slot.getDoctor().getId().equals(doctorId)) {
            throw new DoctorAvailabilityNotFoundException("Créneau introuvable pour ce médecin.");
        }
        availabilityRepository.delete(slot);
    }

    private Doctor ensureDoctor(Long doctorId) {
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Médecin introuvable : " + doctorId));
    }

    private static void validateSlot(AvailabilityRequest request) {
        if (!request.getHeureFin().isAfter(request.getHeureDebut())) {
            throw new InvalidAvailabilityException("L'heure de fin doit être après l'heure de début.");
        }
    }

    private AvailabilityDTO toDto(DoctorAvailability a) {
        return AvailabilityDTO.builder()
                .id(a.getId())
                .doctorId(a.getDoctor().getId())
                .dayOfWeek(a.getDayOfWeek())
                .heureDebut(a.getHeureDebut())
                .heureFin(a.getHeureFin())
                .build();
    }
}
