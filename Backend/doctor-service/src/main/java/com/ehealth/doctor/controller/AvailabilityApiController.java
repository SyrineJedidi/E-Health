package com.ehealth.doctor.controller;

import com.ehealth.doctor.dto.ApiResponse;
import com.ehealth.doctor.dto.AvailabilityDTO;
import com.ehealth.doctor.dto.AvailabilityRequest;
import com.ehealth.doctor.service.DoctorAvailabilityService;
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

/**
 * Exposition {@code /api/availabilities/**} (spec) en plus de
 * {@code /api/doctors/{id}/availabilities}.
 */
@RestController
@RequestMapping("/api/availabilities")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AvailabilityApiController {

    private final DoctorAvailabilityService availabilityService;

    @PostMapping("/doctor/{doctorId}")
    public ResponseEntity<ApiResponse<AvailabilityDTO>> add(
            @PathVariable Long doctorId,
            @RequestBody @Valid AvailabilityRequest request) {
        AvailabilityDTO created = availabilityService.create(doctorId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Disponibilité ajoutée", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AvailabilityDTO>> update(
            @PathVariable Long id,
            @RequestParam Long doctorId,
            @RequestBody @Valid AvailabilityRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Disponibilité mise à jour",
                availabilityService.update(doctorId, id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id, @RequestParam Long doctorId) {
        availabilityService.delete(doctorId, id);
        return ResponseEntity.ok(ApiResponse.success("Disponibilité supprimée", null));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<ApiResponse<List<AvailabilityDTO>>> listByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Disponibilités du médecin", availabilityService.listByDoctor(doctorId)));
    }
}
