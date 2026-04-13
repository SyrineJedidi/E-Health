package com.ehealth.doctor.controller;

import com.ehealth.doctor.dto.ApiResponse;
import com.ehealth.doctor.dto.DoctorCreateRequest;
import com.ehealth.doctor.dto.DoctorDTO;
import com.ehealth.doctor.dto.DoctorUpdateRequest;
import com.ehealth.doctor.dto.PatientSummaryDTO;
import com.ehealth.doctor.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping("/patients")
    public ResponseEntity<ApiResponse<List<PatientSummaryDTO>>> listPatients() {
        List<PatientSummaryDTO> list = doctorService.listPatients();
        return ResponseEntity.ok(ApiResponse.success("Liste des patients", list));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<DoctorDTO>>> search(@RequestParam("q") String q) {
        if (q == null || q.isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Paramètre q requis"));
        }
        return ResponseEntity.ok(ApiResponse.success("Résultats", doctorService.searchDoctors(q)));
    }

    @GetMapping("/specialty/{specialtyId}")
    public ResponseEntity<ApiResponse<List<DoctorDTO>>> bySpecialty(@PathVariable Long specialtyId) {
        return ResponseEntity.ok(
                ApiResponse.success("Médecins par spécialité", doctorService.getDoctorsBySpecialty(specialtyId)));
    }

    @GetMapping("/department/{department}")
    public ResponseEntity<ApiResponse<List<DoctorDTO>>> byDepartment(@PathVariable String department) {
        return ResponseEntity.ok(
                ApiResponse.success("Médecins par service", doctorService.getDoctorsByDepartment(department)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DoctorDTO>>> getAll() {
        List<DoctorDTO> list = doctorService.findAll();
        return ResponseEntity.ok(ApiResponse.success("Liste des médecins", list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DoctorDTO>> getById(@PathVariable Long id) {
        DoctorDTO dto = doctorService.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Médecin trouvé", dto));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DoctorDTO>> create(@RequestBody @Valid DoctorCreateRequest request) {
        DoctorDTO created = doctorService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Médecin créé", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DoctorDTO>> update(
            @PathVariable Long id,
            @RequestBody @Valid DoctorUpdateRequest request) {
        DoctorDTO updated = doctorService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Médecin mis à jour", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        doctorService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Médecin supprimé", null));
    }
}
