package com.ehealth.patient.controller;

import com.ehealth.patient.dto.ApiResponse;
import com.ehealth.patient.dto.DossierMedicalDTO;
import com.ehealth.patient.dto.PatientDTO;
import com.ehealth.patient.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Portail patient", description = "Données du patient connecté (email JWT)")
public class PatientPortalController {

    private final PatientService patientService;

    @GetMapping("/me")
    @Operation(summary = "Profil du patient connecté (email = sujet JWT)")
    public ResponseEntity<ApiResponse<PatientDTO>> me() {
        String email = currentEmail();
        PatientDTO dto = patientService.getPatientByEmail(email);
        return ResponseEntity.ok(ApiResponse.success("Profil patient", dto));
    }

    @GetMapping("/me/dossier")
    @Operation(summary = "Dossier du patient connecté : fiche + rendez-vous")
    public ResponseEntity<ApiResponse<DossierMedicalDTO>> myDossier() {
        String email = currentEmail();
        PatientDTO patient = patientService.getPatientByEmail(email);
        DossierMedicalDTO dossier = patientService.getDossierMedical(patient.getId());
        return ResponseEntity.ok(ApiResponse.success("Mon dossier", dossier));
    }

    /**
     * Inter-services : le paramètre {@code e} doit correspondre au sujet JWT (email), pour éviter
     * l’énumération de fiches patients.
     */
    @GetMapping("/lookup-email")
    @Operation(summary = "Fiche patient par email (uniquement si e = email du JWT)")
    public ResponseEntity<ApiResponse<PatientDTO>> lookupByEmail(@RequestParam("e") String email) {
        String principal = currentEmail();
        if (email == null || email.isBlank() || !principal.equalsIgnoreCase(email.trim())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Accès refusé : email non autorisé"));
        }
        PatientDTO dto = patientService.getPatientByEmail(email.trim());
        return ResponseEntity.ok(ApiResponse.success("Patient trouvé", dto));
    }

    private static String currentEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
            throw new IllegalStateException("Utilisateur non authentifié");
        }
        return auth.getName().trim();
    }
}
