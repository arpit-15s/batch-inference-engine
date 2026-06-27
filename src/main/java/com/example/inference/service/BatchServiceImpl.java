package com.example.inference.service;

import com.example.inference.dto.BatchAckResponse;
import com.example.inference.dto.BatchRequest;
import com.example.inference.dto.BatchResultResponse;
import com.example.inference.exception.BatchNotFoundException;
import com.example.inference.model.BatchJob;
import com.example.inference.model.JobStatus;
import com.example.inference.model.PromptResult;
import com.example.inference.repository.BatchJobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
public class BatchServiceImpl implements BatchService {

    private final BatchJobRepository batchJobRepository;
    private final InferenceClientService inferenceClientService;
    private final AggregationService aggregationService;
    private final Executor batchTaskExecutor;

    public BatchServiceImpl(BatchJobRepository batchJobRepository,
                            InferenceClientService inferenceClientService,
                            AggregationService aggregationService,
                            @Qualifier("batchTaskExecutor") Executor batchTaskExecutor) {
        this.batchJobRepository = batchJobRepository;
        this.inferenceClientService = inferenceClientService;
        this.aggregationService = aggregationService;
        this.batchTaskExecutor = batchTaskExecutor;
    }

    @Override
    public BatchAckResponse acceptBatch(BatchRequest request) {
        BatchJob batchJob = new BatchJob();
        batchJob.setBatchId(UUID.randomUUID().toString());
        batchJob.setStatus(JobStatus.ACCEPTED);
        batchJob.setPrompts(new ArrayList<>(request.getPrompts()));
        batchJob.setTotalPrompts(request.getPrompts().size());
        batchJob.setSubmittedAt(Instant.now());
        batchJobRepository.save(batchJob);

        String batchId = batchJob.getBatchId();
        int totalPrompts = batchJob.getTotalPrompts();
        batchTaskExecutor.execute(() -> processBatch(batchId));

        return new BatchAckResponse(batchId, JobStatus.ACCEPTED, totalPrompts);
    }

    @Override
    public BatchResultResponse getBatch(String batchId) {
        BatchJob batchJob = batchJobRepository.findById(batchId)
                .orElseThrow(() -> new BatchNotFoundException(batchId));

        if (batchJob.getStatus() == JobStatus.COMPLETED || batchJob.getStatus() == JobStatus.PARTIAL) {
            return aggregationService.toResponse(batchJob);
        }

        BatchResultResponse response = new BatchResultResponse();
        response.setBatchId(batchJob.getBatchId());
        response.setStatus(batchJob.getStatus());
        response.setSummary(new BatchResultResponse.Summary(
                batchJob.getTotalPrompts(),
                batchJob.getSucceeded(),
                batchJob.getFailed()));
        response.setSubmittedAt(batchJob.getSubmittedAt());
        return response;
    }

    private void processBatch(String batchId) {
        BatchJob batchJob = batchJobRepository.findById(batchId).orElse(null);
        if (batchJob == null) {
            return;
        }

        batchJob.setStatus(JobStatus.RUNNING);
        batchJobRepository.save(batchJob);

        List<String> prompts = batchJob.getPrompts();
        List<CompletableFuture<PromptResult>> futures = new ArrayList<>(prompts.size());
        for (int i = 0; i < prompts.size(); i++) {
            final int index = i;
            final String prompt = prompts.get(i);
            futures.add(CompletableFuture.supplyAsync(
                    () -> inferenceClientService.infer(prompt, index),
                    batchTaskExecutor));
        }

        List<PromptResult> promptResults = futures.stream()
                .map(CompletableFuture::join)
                .toList();

        batchJob.setCompletedAt(Instant.now());
        aggregationService.aggregate(batchJob, promptResults);
        batchJobRepository.save(batchJob);
    }
}
