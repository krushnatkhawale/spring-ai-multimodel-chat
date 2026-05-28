package com.example.multichat.model;

import java.util.List;

public class ChatResponse {
    private String provider;
    private String response;
    private List<String> errorsLog;
    private long durationMs;

    public ChatResponse() {}

    public ChatResponse(String provider, String response, List<String> errorsLog, long durationMs) {
        this.provider = provider;
        this.response = response;
        this.errorsLog = errorsLog;
        this.durationMs = durationMs;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public List<String> getErrorsLog() {
        return errorsLog;
    }

    public void setErrorsLog(List<String> errorsLog) {
        this.errorsLog = errorsLog;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(long durationMs) {
        this.durationMs = durationMs;
    }
}
