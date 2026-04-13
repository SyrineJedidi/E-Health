package com.ehealth.doctor.controller;

import com.ehealth.doctor.dto.ApiResponse;
import com.ehealth.doctor.dto.DoctorDTO;
import com.ehealth.doctor.dto.PatientSummaryDTO;
import com.ehealth.doctor.dto.RendezVousViewDTO;
import com.ehealth.doctor.service.DoctorPortalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DoctorPortalController {

    private final DoctorPortalService doctorPortalService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<DoctorDTO>> me() {
        DoctorDTO dto = doctorPortalService.getCurrentDoctor();
        return ResponseEntity.ok(ApiResponse.success("Médecin connecté", dto));
    }

    @GetMapping("/me/patients")
    public ResponseEntity<ApiResponse<List<PatientSummaryDTO>>> myPatients() {
        List<PatientSummaryDTO> list = doctorPortalService.getMyPatients();
        return ResponseEntity.ok(ApiResponse.success("Mes patients", list));
    }

    @GetMapping("/me/rendez-vous")
    public ResponseEntity<ApiResponse<List<RendezVousViewDTO>>> myAppointments() {
        List<RendezVousViewDTO> list = doctorPortalService.getMyAppointments();
        return ResponseEntity.ok(ApiResponse.success("Mes rendez-vous", list));
    }
}
