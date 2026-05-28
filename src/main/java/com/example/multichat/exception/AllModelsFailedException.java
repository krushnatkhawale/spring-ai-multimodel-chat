package com.example.multichat.exception;

import java.util.List;

public class AllModelsFailedException extends RuntimeException {
    private final List<String> errorsLog;
    private final long totalDuration;

    public AllModelsFailedException(String message, List<String> errorsLog, long totalDuration) {
        super(message);
        this.errorsLog = errorsLog;
        this.totalDuration = totalDuration;
    }

    public List<String> getErrorsLog() {
        return errorsLog;
    }

    public long getTotalDuration() {
        return totalDuration;
    }
}