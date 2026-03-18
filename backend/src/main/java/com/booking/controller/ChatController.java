package com.booking.controller;

import com.booking.ai.GeminiChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final GeminiChatService chatService;

    public ChatController(GeminiChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<?> chat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        if (message == null || message.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Message is required"));
        }

        String response = chatService.chat(message);
        return ResponseEntity.ok(Map.of("response", response));
    }
}
