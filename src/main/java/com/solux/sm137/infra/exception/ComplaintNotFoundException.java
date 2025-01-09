package com.solux.sm137.infra.exception;

public class ComplaintNotFoundException extends RuntimeException {
    public ComplaintNotFoundException(Long complaintId) {
        super("Complaint with id " + complaintId + " not found");
    }
}

