package com.example.inference.service;

import com.example.inference.dto.BatchResultResponse;
import com.example.inference.model.BatchJob;
import com.example.inference.model.JobStatus;
import com.example.inference.model.PromptResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AggregationServiceImpl implements AggregationService {

    @Override
    public BatchResultResponse aggregate(BatchJob batchJob, List<PromptResult> promptResults) {
        List<BatchResultResponse.InferenceResultItem> successItems = new ArrayList<>();
        int succeeded = 0;
        int failed = 0;

        for (PromptResult result : promptResults) {
            if (result.isSuccess()) {
                succeeded++;
                BatchResultResponse.InferenceResultItem item = new BatchResultResponse.InferenceResultItem();
                item.setIndex(result.getIndex());
                item.setPrompt(result.getPrompt());
                item.setInference(result.getInference());
                item.setLatencyMs(result.getLatencyMs());
                successItems.add(item);
            } else {
                failed++;
            }
        }

        JobStatus status = failed == 0 ? JobStatus.COMPLETED : JobStatus.PARTIAL;
        batchJob.setStatus(status);
        batchJob.setResults(promptResults);
        batchJob.setSucceeded(succeeded);
        batchJob.setFailed(failed);

        return toResponse(batchJob, successItems);
    }

    @Override
    public BatchResultResponse toResponse(BatchJob batchJob) {
        List<BatchResultResponse.InferenceResultItem> successItems = batchJob.getResults().stream()
                .filter(PromptResult::isSuccess)
                .map(this::toItem)
                .toList();
        return toResponse(batchJob, successItems);
    }

    private BatchResultResponse toResponse(BatchJob batchJob,
                                           List<BatchResultResponse.InferenceResultItem> successItems) {
        BatchResultResponse response = new BatchResultResponse();
        response.setBatchId(batchJob.getBatchId());
        response.setStatus(batchJob.getStatus());
        response.setSummary(new BatchResultResponse.Summary(
                batchJob.getTotalPrompts(),
                batchJob.getSucceeded(),
                batchJob.getFailed()));
        response.setResults(successItems);
        response.setSubmittedAt(batchJob.getSubmittedAt());
        response.setCompletedAt(batchJob.getCompletedAt());
        return response;
    }

    private BatchResultResponse.InferenceResultItem toItem(PromptResult result) {
        BatchResultResponse.InferenceResultItem item = new BatchResultResponse.InferenceResultItem();
        item.setIndex(result.getIndex());
        item.setPrompt(result.getPrompt());
        item.setInference(result.getInference());
        item.setLatencyMs(result.getLatencyMs());
        return item;
    }
}
