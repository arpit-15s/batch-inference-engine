package com.example.inference.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class BatchJob {

    private String batchId;
    private JobStatus status;
    private List<String> prompts = new ArrayList<>();
    private List<PromptResult> results = new ArrayList<>();
    private int totalPrompts;
    private int succeeded;
    private int failed;
    private Instant submittedAt;
    private Instant completedAt;

    public BatchJob() {
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

    public List<String> getPrompts() {
        return prompts;
    }

    public void setPrompts(List<String> prompts) {
        this.prompts = prompts;
    }

    public List<PromptResult> getResults() {
        return results;
    }

    public void setResults(List<PromptResult> results) {
        this.results = results;
    }

    public int getTotalPrompts() {
        return totalPrompts;
    }

    public void setTotalPrompts(int totalPrompts) {
        this.totalPrompts = totalPrompts;
    }

    public int getSucceeded() {
        return succeeded;
    }

    public void setSucceeded(int succeeded) {
        this.succeeded = succeeded;
    }

    public int getFailed() {
        return failed;
    }

    public void setFailed(int failed) {
        this.failed = failed;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }
}
