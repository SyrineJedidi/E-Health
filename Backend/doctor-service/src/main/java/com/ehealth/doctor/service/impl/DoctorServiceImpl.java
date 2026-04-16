package com.ehealth.doctor.service.impl;

import com.ehealth.doctor.dto.AvailabilityDTO;
import com.ehealth.doctor.dto.AvailabilityRequest;
import com.ehealth.doctor.dto.DoctorCreateRequest;
import com.ehealth.doctor.dto.DoctorDTO;
import com.ehealth.doctor.dto.DoctorUpdateRequest;
import com.ehealth.doctor.dto.PatientSummaryDTO;
import com.ehealth.doctor.exception.DoctorEmailConflictException;
import com.ehealth.doctor.exception.DoctorNotFoundException;
import com.ehealth.doctor.exception.InvalidAvailabilityException;
import com.ehealth.doctor.exception.SpecialtyNotFoundException;
import com.ehealth.doctor.messaging.DoctorProfileEvent;
import com.ehealth.doctor.messaging.DoctorProfileEventPublisher;
import com.ehealth.doctor.model.Doctor;
import com.ehealth.doctor.model.DoctorAvailability;
import com.ehealth.doctor.model.Specialty;
import com.ehealth.doctor.repository.DoctorRepository;
import com.ehealth.doctor.repository.SpecialtyRepository;
import com.ehealth.doctor.service.DoctorService;
import com.ehealth.doctor.service.PatientLookupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final SpecialtyRepository specialtyRepository;
    private final PatientLookupService patientLookupService;
    private final DoctorProfileEventPublisher doctorProfileEventPublisher;

    @Override
    @Transactional(readOnly = true)
    public List<DoctorDTO> findAll() {
        return doctorRepository.findAllWithSpecialty().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorDTO findById(Long id) {
        Doctor doctor = doctorRepository.findByIdWithSpecialty(id)
                .orElseThrow(() -> new DoctorNotFoundException("Médecin introuvable : " + id));
        return toDto(doctor);
    }

    @Override
    @Transactional
    public DoctorDTO create(DoctorCreateRequest request) {
        String email = request.getEmail().trim();
        if (doctorRepository.existsByEmailIgnoreCase(email)) {
            throw new DoctorEmailConflictException("Un médecin avec cet email existe déjà.");
        }
        String reg = StringUtils.hasText(request.getRegistrationNumber())
                ? request.getRegistrationNumber().trim()
                : "AUTO-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        if (doctorRepository.existsByRegistrationNumberIgnoreCase(reg)) {
            throw new DoctorEmailConflictException("Numéro d’enregistrement déjà utilisé : " + reg);
        }
        Specialty specialty = specialtyRepository.findById(request.getSpecialtyId())
                .orElseThrow(() -> new SpecialtyNotFoundException("Spécialité introuvable : " + request.getSpecialtyId()));
        Doctor doctor = Doctor.builder()
                .nom(request.getNom().trim())
                .prenom(request.getPrenom().trim())
                .email(email)
                .specialty(specialty)
                .telephone(trimToNull(request.getTelephone()))
                .service(trimToNull(request.getService()))
                .registrationNumber(reg)
                .department(trimToNull(request.getDepartment()))
                .active(true)
                .build();
        replaceAvailabilities(doctor, request.getAvailabilities());
        doctor = doctorRepository.save(doctor);
        Doctor reloaded = doctorRepository.findByIdWithSpecialty(doctor.getId()).orElseThrow();
        publishDoctorProfile(reloaded, "CREATED");
        return toDto(reloaded);
    }

    @Override
    @Transactional
    public DoctorDTO update(Long id, DoctorUpdateRequest request) {
        Doctor doctor = doctorRepository.findByIdWithSpecialty(id)
                .orElseThrow(() -> new DoctorNotFoundException("Médecin introuvable : " + id));
        if (StringUtils.hasText(request.getEmail())) {
            String email = request.getEmail().trim();
            if (doctorRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
                throw new DoctorEmailConflictException("Un médecin avec cet email existe déjà.");
            }
            doctor.setEmail(email);
        }
        if (StringUtils.hasText(request.getNom())) {
            doctor.setNom(request.getNom().trim());
        }
        if (StringUtils.hasText(request.getPrenom())) {
            doctor.setPrenom(request.getPrenom().trim());
        }
        if (request.getSpecialtyId() != null) {
            Specialty specialty = specialtyRepository.findById(request.getSpecialtyId())
                    .orElseThrow(() -> new SpecialtyNotFoundException("Spécialité introuvable : " + request.getSpecialtyId()));
            doctor.setSpecialty(specialty);
        }
        if (request.getTelephone() != null) {
            doctor.setTelephone(trimToNull(request.getTelephone()));
        }
        if (request.getService() != null) {
            doctor.setService(trimToNull(request.getService()));
        }
        if (StringUtils.hasText(request.getRegistrationNumber())) {
            String nr = request.getRegistrationNumber().trim();
            if (doctorRepository.existsByRegistrationNumberIgnoreCaseAndIdNot(nr, id)) {
                throw new DoctorEmailConflictException("Numéro d’enregistrement déjà utilisé : " + nr);
            }
            doctor.setRegistrationNumber(nr);
        }
        if (request.getDepartment() != null) {
            doctor.setDepartment(trimToNull(request.getDepartment()));
        }
        if (request.getActive() != null) {
            doctor.setActive(request.getActive());
        }
        if (request.getAvailabilities() != null) {
            replaceAvailabilities(doctor, request.getAvailabilities());
        }
        doctor = doctorRepository.save(doctor);
        Doctor reloaded = doctorRepository.findByIdWithSpecialty(doctor.getId()).orElseThrow();
        publishDoctorProfile(reloaded, "UPDATED");
        return toDto(reloaded);
    }

    private void publishDoctorProfile(Doctor doctor, String eventType) {
        if (doctor.getSpecialty() == null) {
            return;
        }
        doctorProfileEventPublisher.publish(
                new DoctorProfileEvent(
                        doctor.getId(),
                        doctor.getEmail(),
                        doctor.getNom(),
                        doctor.getPrenom(),
                        doctor.getSpecialty().getCode(),
                        eventType,
                        Instant.now()));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!doctorRepository.existsById(id)) {
            throw new DoctorNotFoundException("Médecin introuvable : " + id);
        }
        doctorRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientSummaryDTO> listPatients() {
        return patientLookupService.listPatientsViaPatientService();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorDTO> searchDoctors(String keyword) {
        return doctorRepository.searchByNomContainingWithSpecialty(keyword).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorDTO> getDoctorsBySpecialty(Long specialtyId) {
        return doctorRepository.findBySpecialtyIdWithSpecialty(specialtyId).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorDTO> getDoctorsByDepartment(String department) {
        return doctorRepository.findByDepartmentWithSpecialty(department).stream()
                .map(this::toDto)
                .toList();
    }

    private DoctorDTO toDto(Doctor d) {
        List<AvailabilityDTO> avails =
                d.getAvailabilities() == null || d.getAvailabilities().isEmpty()
                        ? List.of()
                        : d.getAvailabilities().stream()
                                .sorted(
                                        Comparator.comparing(DoctorAvailability::getDayOfWeek)
                                                .thenComparing(DoctorAvailability::getHeureDebut))
                                .map(
                                        a ->
                                                AvailabilityDTO.builder()
                                                        .id(a.getId())
                                                        .doctorId(d.getId())
                                                        .dayOfWeek(a.getDayOfWeek())
                                                        .heureDebut(a.getHeureDebut())
                                                        .heureFin(a.getHeureFin())
                                                        .build())
                                .toList();
        return DoctorDTO.builder()
                .id(d.getId())
                .nom(d.getNom())
                .prenom(d.getPrenom())
                .email(d.getEmail())
                .specialtyId(d.getSpecialty().getId())
                .specialite(d.getSpecialty().getLabel())
                .telephone(d.getTelephone())
                .service(d.getService())
                .registrationNumber(d.getRegistrationNumber())
                .department(d.getDepartment())
                .active(d.isActive())
                .createdAt(d.getCreatedAt())
                .availabilities(avails)
                .build();
    }

    private void replaceAvailabilities(Doctor doctor, List<AvailabilityRequest> slots) {
        if (slots == null) {
            return;
        }
        doctor.getAvailabilities().clear();
        for (AvailabilityRequest req : slots) {
            validateSlot(req);
            DoctorAvailability slot =
                    DoctorAvailability.builder()
                            .doctor(doctor)
                            .dayOfWeek(req.getDayOfWeek())
                            .heureDebut(req.getHeureDebut())
                            .heureFin(req.getHeureFin())
                            .build();
            doctor.getAvailabilities().add(slot);
        }
    }

    private static void validateSlot(AvailabilityRequest request) {
        if (!request.getHeureFin().isAfter(request.getHeureDebut())) {
            throw new InvalidAvailabilityException("L'heure de fin doit être après l'heure de début.");
        }
    }

    private static String trimToNull(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
