package com.ehealth.patient.exception;

public class PatientNotFoundException extends ResourceNotFoundException {

    public PatientNotFoundException(Long id) {
        super("Patient not found with id: " + id);
    }
}
