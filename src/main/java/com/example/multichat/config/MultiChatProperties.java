package com.example.multichat.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "multichat")
public class MultiChatProperties {

    private List<String> fallbackSequence = new ArrayList<>();

    public List<String> getFallbackSequence() {
        return fallbackSequence;
    }

    public void setFallbackSequence(List<String> fallbackSequence) {
        this.fallbackSequence = fallbackSequence;
    }
}
