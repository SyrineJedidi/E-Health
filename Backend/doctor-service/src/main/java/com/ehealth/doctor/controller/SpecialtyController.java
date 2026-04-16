package com.ehealth.doctor.controller;

import com.ehealth.doctor.dto.ApiResponse;
import com.ehealth.doctor.dto.SpecialtyDTO;
import com.ehealth.doctor.service.SpecialtyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Référentiel spécialités en <strong>lecture seule</strong> (sélection lors du CRUD médecin).
 * Pas de création / mise à jour / suppression via API.
 */
@RestController
@RequestMapping("/api/specialties")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SpecialtyDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success("Spécialités", specialtyService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SpecialtyDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Spécialité", specialtyService.findById(id)));
    }
}
