package com.banking.support.ticket_service.dto;

import com.banking.support.ticket_service.entity.Ticket;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateTicketStatusRequest {

    @NotNull(message = "Status is required")
    private Ticket.TicketStatus status;
}
