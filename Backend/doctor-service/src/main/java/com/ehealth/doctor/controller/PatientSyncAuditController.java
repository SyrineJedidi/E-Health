package com.ehealth.doctor.controller;

import com.ehealth.doctor.dto.ApiResponse;
import com.ehealth.doctor.dto.PatientProfileEventLogDto;
import com.ehealth.doctor.repository.PatientProfileEventLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Consultation de l’audit des événements « profil patient » (démonstration scénario RabbitMQ professionnel).
 */
@RestController
@RequestMapping("/api/doctors/sync")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PatientSyncAuditController {

    private final PatientProfileEventLogRepository patientProfileEventLogRepository;

    @GetMapping("/patient-profile-events")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<PatientProfileEventLogDto>>> listRecent() {
        var pageable = PageRequest.of(0, 50, Sort.by(Sort.Direction.DESC, "receivedAt"));
        List<PatientProfileEventLogDto> list =
                patientProfileEventLogRepository.findAllByOrderByReceivedAtDesc(pageable).stream()
                        .map(PatientProfileEventLogDto::fromEntity)
                        .toList();
        return ResponseEntity.ok(ApiResponse.success("Événements profil patient (sync async)", list));
    }
}
