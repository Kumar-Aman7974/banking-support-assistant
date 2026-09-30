package com.banking.support.ticket_service.dto;

import com.banking.support.ticket_service.entity.Ticket;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TicketResponse {

    private Long id;
    private String subject;
    private String description;
    private Ticket.TicketStatus status;
    private Ticket.Priority priority;
    private Long customerId;
    private String customerName;
    private Long assignedAgentId;
    private String assignedAgentName;
    private String suggestedResolution;
    private String finalResolution;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;

}
