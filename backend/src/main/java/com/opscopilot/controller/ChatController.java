package com.opscopilot.controller;

import com.opscopilot.dto.*;
import com.opscopilot.service.AlertService;
import com.opscopilot.service.AssetService;
import com.opscopilot.service.ChatService;
import com.opscopilot.service.KnowledgeService;
import com.opscopilot.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * AI对话控制器 - 智能体核心API
 */
@Tag(name = "智能对话", description = "AI智能体对话接口")
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    
    private final ChatService chatService;
    
    @PostMapping
    @Operation(summary = "发送消息", description = "与智能体对话")
    public Result<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        return chatService.chat(request);
    }
}
