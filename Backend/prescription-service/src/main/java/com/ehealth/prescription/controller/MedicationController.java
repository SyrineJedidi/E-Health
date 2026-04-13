package com.ehealth.prescription.controller;

import com.ehealth.prescription.dto.MedicationCatalogRequest;
import com.ehealth.prescription.dto.MedicationCatalogResponse;
import com.ehealth.prescription.service.MedicationService;
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
@RequestMapping("/api/medications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MedicationController {

    private final MedicationService medicationService;

    @GetMapping
    public ResponseEntity<List<MedicationCatalogResponse>> getAll() {
        return ResponseEntity.ok(medicationService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<List<MedicationCatalogResponse>> search(@RequestParam(name = "q", required = false) String q) {
        return ResponseEntity.ok(medicationService.search(q));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicationCatalogResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(medicationService.findById(id));
    }

    @PostMapping
    public ResponseEntity<MedicationCatalogResponse> create(@Valid @RequestBody MedicationCatalogRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(medicationService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicationCatalogResponse> update(
            @PathVariable Long id, @Valid @RequestBody MedicationCatalogRequest request) {
        return ResponseEntity.ok(medicationService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        medicationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
