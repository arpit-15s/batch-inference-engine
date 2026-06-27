package com.example.inference.dto;

import com.example.inference.model.JobStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class BatchResultResponse {

    private String batchId;
    private JobStatus status;
    private Summary summary;
    private List<InferenceResultItem> results = new ArrayList<>();
    private Instant submittedAt;
    private Instant completedAt;

    public BatchResultResponse() {
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

    public Summary getSummary() {
        return summary;
    }

    public void setSummary(Summary summary) {
        this.summary = summary;
    }

    public List<InferenceResultItem> getResults() {
        return results;
    }

    public void setResults(List<InferenceResultItem> results) {
        this.results = results;
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

    public static class Summary {

        private int total;
        private int succeeded;
        private int failed;

        public Summary() {
        }

        public Summary(int total, int succeeded, int failed) {
            this.total = total;
            this.succeeded = succeeded;
            this.failed = failed;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
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
    }

    public static class InferenceResultItem {

        private int index;
        private String prompt;
        private String inference;
        private long latencyMs;

        public InferenceResultItem() {
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getPrompt() {
            return prompt;
        }

        public void setPrompt(String prompt) {
            this.prompt = prompt;
        }

        public String getInference() {
            return inference;
        }

        public void setInference(String inference) {
            this.inference = inference;
        }

        public long getLatencyMs() {
            return latencyMs;
        }

        public void setLatencyMs(long latencyMs) {
            this.latencyMs = latencyMs;
        }
    }
}
