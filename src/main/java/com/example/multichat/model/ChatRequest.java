package com.example.multichat.model;

import java.util.List;

public class ChatRequest {
    private String prompt;
    private List<String> overrideSequence;

    public ChatRequest() {}

    public ChatRequest(String prompt) {
        this.prompt = prompt;
    }

    public ChatRequest(String prompt, List<String> overrideSequence) {
        this.prompt = prompt;
        this.overrideSequence = overrideSequence;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public List<String> getOverrideSequence() {
        return overrideSequence;
    }

    public void setOverrideSequence(List<String> overrideSequence) {
        this.overrideSequence = overrideSequence;
    }
}
