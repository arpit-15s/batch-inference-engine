package com.example.inference.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public class BatchRequest {

    @NotEmpty
    @Size(max = 1000)
    private List<@NotEmpty String> prompts;

    public BatchRequest() {
    }

    public BatchRequest(List<String> prompts) {
        this.prompts = prompts;
    }

    public List<String> getPrompts() {
        return prompts;
    }

    public void setPrompts(List<String> prompts) {
        this.prompts = prompts;
    }
}
