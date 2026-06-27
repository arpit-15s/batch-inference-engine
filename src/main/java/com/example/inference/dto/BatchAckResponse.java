package com.example.inference.dto;

import com.example.inference.model.JobStatus;

public class BatchAckResponse {

    private String batchId;
    private JobStatus status;
    private int totalPrompts;

    public BatchAckResponse() {
    }

    public BatchAckResponse(String batchId, JobStatus status, int totalPrompts) {
        this.batchId = batchId;
        this.status = status;
        this.totalPrompts = totalPrompts;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public int getTotalPrompts() {
        return totalPrompts;
    }

    public void setTotalPrompts(int totalPrompts) {
        this.totalPrompts = totalPrompts;
    }
}
