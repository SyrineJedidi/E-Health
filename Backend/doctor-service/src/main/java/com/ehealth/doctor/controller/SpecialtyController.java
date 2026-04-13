package com.ehealth.doctor.controller;

import com.ehealth.doctor.dto.ApiResponse;
import com.ehealth.doctor.dto.SpecialtyDTO;
import com.ehealth.doctor.dto.SpecialtyRequest;
import com.ehealth.doctor.service.SpecialtyService;
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

    @PostMapping
    public ResponseEntity<ApiResponse<SpecialtyDTO>> create(@RequestBody @Valid SpecialtyRequest request) {
        SpecialtyDTO created = specialtyService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Spécialité créée", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SpecialtyDTO>> update(
            @PathVariable Long id,
            @RequestBody @Valid SpecialtyRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Spécialité mise à jour", specialtyService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        specialtyService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Spécialité supprimée", null));
    }
}
