package com.ehealth.patient.controller;

import com.ehealth.patient.dto.ApiResponse;
import com.ehealth.patient.dto.PatientDTO;
import com.ehealth.patient.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/patients/public")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "*")
@Tag(name = "Patients public", description = "Création de fiche patient sans JWT (usage contrôlé)")
public class PublicPatientController {

    private final PatientService patientService;

    @PostMapping("/register")
    @Operation(summary = "Créer un patient (endpoint public)")
    public ResponseEntity<ApiResponse<PatientDTO>> register(
            @RequestBody @Valid @Validated(PatientDTO.OnCreate.class) PatientDTO dto) {
        PatientDTO created = patientService.createPatient(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Patient créé", created));
    }
}
