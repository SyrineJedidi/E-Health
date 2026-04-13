package com.ehealth.prescription.controller;

import com.ehealth.prescription.dto.PatientCommentRequest;
import com.ehealth.prescription.dto.PrescriptionRequest;
import com.ehealth.prescription.dto.PrescriptionResponse;
import com.ehealth.prescription.service.PrescriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @PostMapping
    public ResponseEntity<PrescriptionResponse> create(@Valid @RequestBody PrescriptionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(prescriptionService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<PrescriptionResponse>> getAll() {
        return ResponseEntity.ok(prescriptionService.findAll());
    }

    @GetMapping("/me")
    public ResponseEntity<List<PrescriptionResponse>> myPrescriptions() {
        return ResponseEntity.ok(prescriptionService.findForCurrentPatient());
    }

    @GetMapping("/history")
    public ResponseEntity<List<PrescriptionResponse>> history(
            @RequestParam Long patientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionHistory(patientId, from, to));
    }

    @GetMapping("/patient/{patientId}/active")
    public ResponseEntity<List<PrescriptionResponse>> getActiveByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(prescriptionService.getActivePrescriptionsByPatient(patientId));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<PrescriptionResponse>> getByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(prescriptionService.findByPatientId(patientId));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<PrescriptionResponse>> getByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(prescriptionService.findByDoctorId(doctorId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrescriptionResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(prescriptionService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PrescriptionResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody PrescriptionRequest request) {
        return ResponseEntity.ok(prescriptionService.update(id, request));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<PrescriptionResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(prescriptionService.cancelPrescription(id));
    }

    @PatchMapping("/{id}/patient-comment")
    public ResponseEntity<PrescriptionResponse> updatePatientComment(
            @PathVariable Long id, @Valid @RequestBody PatientCommentRequest request) {
        return ResponseEntity.ok(
                prescriptionService.updatePatientComment(id, request.getComment()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        prescriptionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
