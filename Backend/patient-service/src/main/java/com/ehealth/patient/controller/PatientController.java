package com.ehealth.patient.controller;

import com.ehealth.patient.dto.ApiResponse;
import com.ehealth.patient.dto.DossierMedicalDTO;
import com.ehealth.patient.dto.PatientDTO;
import com.ehealth.patient.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "*")
@Tag(name = "Patients", description = "API de gestion des patients eHealth")
public class PatientController {

    private final PatientService patientService;

    @GetMapping
    @Operation(summary = "Lister tous les patients")
    public ResponseEntity<ApiResponse<List<PatientDTO>>> getAllPatients() {
        List<PatientDTO> list = patientService.getAllPatients();
        return ResponseEntity.ok(ApiResponse.success("Liste des patients", list));
    }

    @GetMapping("/search")
    @Operation(summary = "Recherche par nom (q ou nom, contient, insensible à la casse)")
    public ResponseEntity<ApiResponse<List<PatientDTO>>> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String nom) {
        String keyword = (q != null && !q.isBlank()) ? q : nom;
        if (keyword == null || keyword.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Paramètre de recherche requis : q ou nom"));
        }
        List<PatientDTO> list = patientService.searchPatients(keyword);
        return ResponseEntity.ok(ApiResponse.success("Résultats de la recherche", list));
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Obtenir un patient par email")
    public ResponseEntity<ApiResponse<PatientDTO>> getByEmail(@PathVariable String email) {
        PatientDTO dto = patientService.getPatientByEmail(email);
        return ResponseEntity.ok(ApiResponse.success("Patient trouvé", dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un patient par ID")
    public ResponseEntity<ApiResponse<PatientDTO>> getById(@PathVariable Long id) {
        PatientDTO dto = patientService.getPatientById(id);
        return ResponseEntity.ok(ApiResponse.success("Patient trouvé", dto));
    }

    @PostMapping
    @Operation(summary = "Créer un patient (JWT requis)")
    public ResponseEntity<ApiResponse<PatientDTO>> create(
            @RequestBody @Valid @Validated(PatientDTO.OnCreate.class) PatientDTO dto) {
        PatientDTO created = patientService.createPatient(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Patient créé avec succès", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un patient")
    public ResponseEntity<ApiResponse<PatientDTO>> update(
            @PathVariable Long id, @RequestBody @Valid PatientDTO dto) {
        PatientDTO updated = patientService.updatePatient(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Patient mis à jour", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un patient")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.ok(ApiResponse.success("Patient supprimé", null));
    }

    @GetMapping("/{id}/dossier")
    @Operation(summary = "Dossier médical (patient + rendez-vous)")
    public ResponseEntity<ApiResponse<DossierMedicalDTO>> getDossier(@PathVariable Long id) {
        DossierMedicalDTO dossier = patientService.getDossierMedical(id);
        return ResponseEntity.ok(ApiResponse.success("Dossier médical", dossier));
    }

    @PostMapping("/batch")
    @Operation(summary = "Récupérer plusieurs patients par liste d'IDs")
    public ResponseEntity<ApiResponse<List<PatientDTO>>> getBatch(
            @RequestBody @Valid @NotEmpty(message = "La liste d'identifiants ne peut pas être vide")
                    List<Long> ids) {
        List<PatientDTO> list = patientService.getPatientsByIds(ids);
        return ResponseEntity.ok(ApiResponse.success("Patients (batch)", list));
    }
}
