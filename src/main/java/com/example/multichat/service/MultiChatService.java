package com.example.multichat.service;

import com.example.multichat.exception.AllModelsFailedException;
import com.example.multichat.model.ChatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MultiChatService {
    private static final Logger log = LoggerFactory.getLogger(MultiChatService.class);

    private final com.example.multichat.config.MultiChatProperties multiChatProperties;

    @Autowired(required = false)
    private OpenAiChatModel openAiChatModel;

    @Autowired(required = false)
    private GoogleGenAiChatModel googleGenAiChatModel;

    @Autowired(required = false)
    private OllamaChatModel ollamaChatModel;

    public MultiChatService(com.example.multichat.config.MultiChatProperties multiChatProperties) {
        this.multiChatProperties = multiChatProperties;
    }

    public List<String> getDefaultFallbackSequence() {
        return multiChatProperties.getFallbackSequence();
    }

    public ChatResponse chat(String prompt, List<String> overrideSequence) {
        List<String> sequence = (overrideSequence != null && !overrideSequence.isEmpty()) 
                ? overrideSequence 
                : multiChatProperties.getFallbackSequence();

        List<String> errorsLog = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        for (String provider : sequence) {
            String trimmedProvider = provider.trim().toLowerCase();
            log.info("Attempting chat request with provider: {}", trimmedProvider);

            ChatModel model = getChatModel(trimmedProvider);
            if (model == null) {
                String error = String.format("Provider '%s' is not supported or its bean is not initialized", provider);
                errorsLog.add(error);
                log.warn(error);
                continue;
            }

            try {
                // Call the model and get the response
                String responseText = model.call(prompt);
                
                long duration = System.currentTimeMillis() - startTime;
                log.info("Successfully received response from provider: {} in {}ms", trimmedProvider, duration);
                return new ChatResponse(trimmedProvider, responseText, errorsLog, duration);
            } catch (Exception e) {
                String errorMsg = String.format("Failed using provider '%s'. Error: %s", provider, e.getMessage());
                errorsLog.add(errorMsg);
                log.warn(errorMsg);
            }
        }

        long totalDuration = System.currentTimeMillis() - startTime;
        throw new AllModelsFailedException("All configured model providers failed", errorsLog, totalDuration);
    }

    private ChatModel getChatModel(String provider) {
        return switch (provider) {
            case "openai" -> openAiChatModel;
            case "gemini" -> googleGenAiChatModel;
            case "local", "ollama" -> ollamaChatModel;
            default -> null;
        };
    }
}