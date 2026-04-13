package com.ehealth.doctor.exception;

public class DoctorEmailConflictException extends RuntimeException {

    public DoctorEmailConflictException(String message) {
        super(message);
    }
}
