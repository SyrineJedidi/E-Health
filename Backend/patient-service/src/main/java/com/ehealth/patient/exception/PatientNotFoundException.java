package com.ehealth.patient.exception;

public class PatientNotFoundException extends RuntimeException {

    public PatientNotFoundException(Long id) {
        super("Patient non trouvé avec l'ID : " + id);
    }
}
