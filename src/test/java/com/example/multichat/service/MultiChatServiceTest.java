package com.example.multichat.service;

import com.example.multichat.model.ChatResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MultiChatServiceTest {

    private MultiChatService multiChatService;

    @Mock
    private OpenAiChatModel openAiChatModel;

    @Mock
    private GoogleGenAiChatModel googleGenAiChatModel;

    @Mock
    private OllamaChatModel ollamaChatModel;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // Default fallback sequence: gemini -> openai -> local
        com.example.multichat.config.MultiChatProperties properties = new com.example.multichat.config.MultiChatProperties();
        properties.setFallbackSequence(Arrays.asList("gemini", "openai", "local"));
        
        multiChatService = new MultiChatService(properties);
        
        // Manually inject mock beans using Spring Utility (reflection)
        ReflectionTestUtils.setField(multiChatService, "openAiChatModel", openAiChatModel);
        ReflectionTestUtils.setField(multiChatService, "googleGenAiChatModel", googleGenAiChatModel);
        ReflectionTestUtils.setField(multiChatService, "ollamaChatModel", ollamaChatModel);
    }

    @Test
    public void testFirstModelSucceeds() {
        // Arrange
        String prompt = "Hello";
        when(googleGenAiChatModel.call(prompt)).thenReturn("Response from Gemini");

        // Act
        ChatResponse response = multiChatService.chat(prompt, null);

        // Assert
        assertEquals("gemini", response.getProvider());
        assertEquals("Response from Gemini", response.getResponse());
        assertTrue(response.getErrorsLog().isEmpty());
        
        // Verify only gemini was called
        verify(googleGenAiChatModel, times(1)).call(prompt);
        verify(openAiChatModel, never()).call(anyString());
        verify(ollamaChatModel, never()).call(anyString());
    }

    @Test
    public void testFirstFailsSecondSucceeds() {
        // Arrange
        String prompt = "Hello";
        when(googleGenAiChatModel.call(prompt)).thenThrow(new RuntimeException("Gemini quota exceeded"));
        when(openAiChatModel.call(prompt)).thenReturn("Response from OpenAI");

        // Act
        ChatResponse response = multiChatService.chat(prompt, null);

        // Assert
        assertEquals("openai", response.getProvider());
        assertEquals("Response from OpenAI", response.getResponse());
        assertEquals(1, response.getErrorsLog().size());
        assertTrue(response.getErrorsLog().get(0).contains("Failed using provider 'gemini'"));
        assertTrue(response.getErrorsLog().get(0).contains("Gemini quota exceeded"));

        // Verify calls
        verify(googleGenAiChatModel, times(1)).call(prompt);
        verify(openAiChatModel, times(1)).call(prompt);
        verify(ollamaChatModel, never()).call(anyString());
    }

    @Test
    public void testAllModelsFail() {
        // Arrange
        String prompt = "Hello";
        when(googleGenAiChatModel.call(prompt)).thenThrow(new RuntimeException("Gemini offline"));
        when(openAiChatModel.call(prompt)).thenThrow(new RuntimeException("OpenAI bad credentials"));
        when(ollamaChatModel.call(prompt)).thenThrow(new RuntimeException("Ollama connection refused"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            multiChatService.chat(prompt, null);
        });

        assertTrue(exception.getMessage().contains("All configured model providers failed"));
        assertTrue(exception.getMessage().contains("Gemini offline"));
        assertTrue(exception.getMessage().contains("OpenAI bad credentials"));
        assertTrue(exception.getMessage().contains("Ollama connection refused"));

        // Verify calls
        verify(googleGenAiChatModel, times(1)).call(prompt);
        verify(openAiChatModel, times(1)).call(prompt);
        verify(ollamaChatModel, times(1)).call(prompt);
    }

    @Test
    public void testOverrideSequence() {
        // Arrange
        String prompt = "Hello";
        List<String> overrideSeq = Arrays.asList("local", "openai");
        when(ollamaChatModel.call(prompt)).thenThrow(new RuntimeException("Local error"));
        when(openAiChatModel.call(prompt)).thenReturn("OpenAI response");

        // Act
        ChatResponse response = multiChatService.chat(prompt, overrideSeq);

        // Assert
        assertEquals("openai", response.getProvider());
        assertEquals("OpenAI response", response.getResponse());
        assertEquals(1, response.getErrorsLog().size());
        assertTrue(response.getErrorsLog().get(0).contains("local"));

        // Verify Gemini was NEVER called since it wasn't in the override sequence
        verify(googleGenAiChatModel, never()).call(anyString());
        verify(ollamaChatModel, times(1)).call(prompt);
        verify(openAiChatModel, times(1)).call(prompt);
    }
}
