package com.banking.support.ticket_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignAgentRequest {

    @NotNull(message = "Agent ID is required")
    private Long agentId;

}
