package com.banking.support.ticket_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "ai-service")
public interface AiServiceClient {

    @PostMapping("/api/v1/chat/ask")
    Map<String, String> askQuestion(@RequestBody Map<String, String> request);


}
