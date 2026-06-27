package com.example.inference.exception;

public class BatchNotFoundException extends RuntimeException {

    public BatchNotFoundException(String batchId) {
        super("Batch not found: " + batchId);
    }
}
