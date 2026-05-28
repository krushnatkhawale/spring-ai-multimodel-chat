package com.example.multichat.controller;

import com.example.multichat.model.ChatRequest;
import com.example.multichat.model.ChatResponse;
import com.example.multichat.service.MultiChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ChatController {

    private final MultiChatService multiChatService;

    public ChatController(MultiChatService multiChatService) {
        this.multiChatService = multiChatService;
    }

    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody ChatRequest request) {
        if (request.getPrompt() == null || request.getPrompt().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Prompt cannot be empty"));
        }
        
        ChatResponse response = multiChatService.chat(request.getPrompt(), request.getOverrideSequence());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/config")
    public ResponseEntity<List<String>> getConfig() {
        return ResponseEntity.ok(multiChatService.getDefaultFallbackSequence());
    }
}