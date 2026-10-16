package com.banking.support.aiservice.controller;

import com.banking.support.aiservice.service.RagQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class RagController {

    private final RagQueryService ragQueryService;

    @PostMapping("/suggest")
    public ResponseEntity<Map<String, String>> suggest(@RequestBody Map<String, String> request) {
        String description = request.get("ticketDescription");
        String resolution = ragQueryService.query(description);
        return ResponseEntity.ok(Map.of("suggestedResolution", resolution));
    }
}