package com.banking.support.ticket_service.service;


import com.banking.support.ticket_service.dto.CreateTicketRequest;
import com.banking.support.ticket_service.dto.TicketResponse;
import com.banking.support.ticket_service.entity.Ticket;
import com.banking.support.ticket_service.entity.User;
import com.banking.support.ticket_service.exception.ResourceNotFoundException;
import com.banking.support.ticket_service.repository.TicketRepository;
import com.banking.support.ticket_service.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    @Transactional
    public TicketResponse createTicket(CreateTicketRequest request)
    {
        User customer = userRepository.findById(request.CustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));


        Ticket ticket = Ticket.builder()
                .subject(request.getSubject())
                .description(request.getDescription())
                .status(Ticket.TicketStatus.OPEN)
                .priority(request.getPriority())
                .customer(customer)
                .build();

        Ticket saved = ticketRepository.save(ticket);
        return toResponse(saved);
    }

    public List<TicketResponse> getAllTickets() {

        return ticketRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public TicketResponse getTicket(Long id) {

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));
        return toResponse(ticket);
    }

    private TicketResponse toResponse(Ticket ticket) {

        return TicketResponse.builder()
                .id(ticket.getId())
                .subject(ticket.getSubject())
                .description(ticket.getDescription())
                .status(ticket.getStatus())
                .priority(ticket.getPriority())
                .customerId(ticket.getCustomer().getId())
                .customerName(ticket.getCustomer().getFullName())
                .assignedAgentId(ticket.getAssignedAgent() != null ? ticket.getAssignedAgent().getId() : null)
                .assignedAgentName(ticket.getAssignedAgent() != null ? ticket.getAssignedAgent().getFullName() : null)
                .suggestedResolution(ticket.getSuggestedResolution())
                .finalResolution(ticket.getFinalResolution())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .resolvedAt(ticket.getResolvedAt())
                .build();
    }
}
