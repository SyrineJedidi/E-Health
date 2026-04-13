package com.ehealth.doctor.controller;

import com.ehealth.doctor.dto.ApiResponse;
import com.ehealth.doctor.dto.AvailabilityDTO;
import com.ehealth.doctor.dto.AvailabilityRequest;
import com.ehealth.doctor.service.DoctorAvailabilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/doctors/{doctorId}/availabilities")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DoctorAvailabilityController {

    private final DoctorAvailabilityService availabilityService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AvailabilityDTO>>> list(@PathVariable Long doctorId) {
        return ResponseEntity.ok(
                ApiResponse.success("Disponibilités du médecin", availabilityService.listByDoctor(doctorId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AvailabilityDTO>> create(
            @PathVariable Long doctorId,
            @RequestBody @Valid AvailabilityRequest request) {
        AvailabilityDTO created = availabilityService.create(doctorId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Disponibilité ajoutée", created));
    }

    @PutMapping("/{availabilityId}")
    public ResponseEntity<ApiResponse<AvailabilityDTO>> update(
            @PathVariable Long doctorId,
            @PathVariable Long availabilityId,
            @RequestBody @Valid AvailabilityRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Disponibilité mise à jour",
                availabilityService.update(doctorId, availabilityId, request)));
    }

    @DeleteMapping("/{availabilityId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long doctorId,
            @PathVariable Long availabilityId) {
        availabilityService.delete(doctorId, availabilityId);
        return ResponseEntity.ok(ApiResponse.success("Disponibilité supprimée", null));
    }
}
