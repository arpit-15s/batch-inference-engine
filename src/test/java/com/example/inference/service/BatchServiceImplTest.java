package com.example.inference.service;

import com.example.inference.dto.BatchAckResponse;
import com.example.inference.dto.BatchRequest;
import com.example.inference.dto.BatchResultResponse;
import com.example.inference.exception.BatchNotFoundException;
import com.example.inference.model.BatchJob;
import com.example.inference.model.JobStatus;
import com.example.inference.model.PromptResult;
import com.example.inference.repository.BatchJobRepository;
import com.example.inference.repository.InMemoryBatchJobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.Executor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BatchServiceImplTest {

    private BatchJobRepository batchJobRepository;

    @Mock
    private InferenceClientService inferenceClientService;

    @Mock
    private AggregationService aggregationService;

    private BatchServiceImpl batchService;

    @BeforeEach
    void setUp() {
        batchJobRepository = new InMemoryBatchJobRepository();
        Executor syncExecutor = Runnable::run;
        batchService = new BatchServiceImpl(
                batchJobRepository,
                inferenceClientService,
                aggregationService,
                syncExecutor);
    }

    @Test
    void acceptBatchPersistsJobAndReturnsAck() {
        BatchRequest request = new BatchRequest(List.of("prompt-1", "prompt-2"));

        PromptResult success = new PromptResult();
        success.setIndex(0);
        success.setPrompt("prompt-1");
        success.setInference("ok-1");
        success.setSuccess(true);
        when(inferenceClientService.infer("prompt-1", 0)).thenReturn(success);

        PromptResult success2 = new PromptResult();
        success2.setIndex(1);
        success2.setPrompt("prompt-2");
        success2.setInference("ok-2");
        success2.setSuccess(true);
        when(inferenceClientService.infer("prompt-2", 1)).thenReturn(success2);

        BatchResultResponse aggregated = new BatchResultResponse();
        aggregated.setStatus(JobStatus.COMPLETED);
        when(aggregationService.aggregate(any(BatchJob.class), any())).thenReturn(aggregated);

        BatchAckResponse ack = batchService.acceptBatch(request);

        assertThat(ack.getStatus()).isEqualTo(JobStatus.ACCEPTED);
        assertThat(ack.getTotalPrompts()).isEqualTo(2);
        assertThat(ack.getBatchId()).isNotBlank();
        verify(inferenceClientService).infer("prompt-1", 0);
        verify(inferenceClientService).infer("prompt-2", 1);
        verify(aggregationService).aggregate(any(BatchJob.class), any());
    }

    @Test
    void getBatchReturnsCompletedResponse() {
        BatchJob job = new BatchJob();
        job.setBatchId("batch-1");
        job.setStatus(JobStatus.COMPLETED);
        job.setTotalPrompts(1);
        batchJobRepository.save(job);

        BatchResultResponse response = new BatchResultResponse();
        response.setBatchId("batch-1");
        response.setStatus(JobStatus.COMPLETED);
        when(aggregationService.toResponse(job)).thenReturn(response);

        BatchResultResponse result = batchService.getBatch("batch-1");

        assertThat(result.getBatchId()).isEqualTo("batch-1");
        assertThat(result.getStatus()).isEqualTo(JobStatus.COMPLETED);
    }

    @Test
    void getBatchRunningReturnsInProgressResponse() {
        BatchJob job = new BatchJob();
        job.setBatchId("batch-1");
        job.setStatus(JobStatus.RUNNING);
        job.setTotalPrompts(3);
        batchJobRepository.save(job);

        BatchResultResponse result = batchService.getBatch("batch-1");

        assertThat(result.getStatus()).isEqualTo(JobStatus.RUNNING);
        assertThat(result.getSummary().getTotal()).isEqualTo(3);
        assertThat(result.getResults()).isEmpty();
    }

    @Test
    void getBatchNotFoundThrows() {
        assertThatThrownBy(() -> batchService.getBatch("missing"))
                .isInstanceOf(BatchNotFoundException.class);
    }
}
