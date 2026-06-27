package com.example.inference.controller;

import com.example.inference.dto.BatchAckResponse;
import com.example.inference.dto.BatchResultResponse;
import com.example.inference.exception.GlobalExceptionHandler;
import com.example.inference.model.JobStatus;
import com.example.inference.service.BatchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BatchController.class)
@Import(GlobalExceptionHandler.class)
class BatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BatchService batchService;

    @Test
    void submitBatchReturnsAccepted() throws Exception {
        BatchAckResponse ack = new BatchAckResponse("batch-1", JobStatus.ACCEPTED, 2);
        when(batchService.acceptBatch(org.mockito.ArgumentMatchers.any())).thenReturn(ack);

        mockMvc.perform(post("/api/v1/batches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new com.example.inference.dto.BatchRequest(List.of("a", "b")))))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.batchId").value("batch-1"))
                .andExpect(jsonPath("$.status").value("ACCEPTED"))
                .andExpect(jsonPath("$.totalPrompts").value(2));
    }

    @Test
    void submitBatchWithEmptyPromptsReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/batches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"prompts\":[]}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBatchReturnsOk() throws Exception {
        BatchResultResponse response = new BatchResultResponse();
        response.setBatchId("batch-1");
        response.setStatus(JobStatus.COMPLETED);
        response.setSummary(new BatchResultResponse.Summary(1, 1, 0));
        when(batchService.getBatch("batch-1")).thenReturn(response);

        mockMvc.perform(get("/api/v1/batches/batch-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.batchId").value("batch-1"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void getBatchNotFoundReturns404() throws Exception {
        when(batchService.getBatch("missing"))
                .thenThrow(new com.example.inference.exception.BatchNotFoundException("missing"));

        mockMvc.perform(get("/api/v1/batches/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Batch not found: missing"));
    }
}
