package com.banking.support.ticket_service.service;


import com.banking.support.ticket_service.dto.AddCommentRequest;
import com.banking.support.ticket_service.dto.CommentResponse;
import com.banking.support.ticket_service.dto.CreateTicketRequest;
import com.banking.support.ticket_service.dto.TicketResponse;
import com.banking.support.ticket_service.entity.Ticket;
import com.banking.support.ticket_service.entity.TicketComment;
import com.banking.support.ticket_service.entity.User;
import com.banking.support.ticket_service.exception.ResourceNotFoundException;
import com.banking.support.ticket_service.repository.TicketCommentRepository;
import com.banking.support.ticket_service.repository.TicketRepository;
import com.banking.support.ticket_service.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    private final TicketCommentRepository ticketCommentRepository;

    @Transactional
    public TicketResponse createTicket(CreateTicketRequest request, String customerEmail)
    {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow( () -> new ResourceNotFoundException((
                        "Customer not found: " + customerEmail
                        )));


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

    public Page<TicketResponse> getAllTickets(
            Ticket.TicketStatus status,
            Ticket.Priority priority,
            Long customerId,
            Pageable pageable
    ) {

        Page<Ticket> page;

        if (status != null) {
            page = ticketRepository.findByStatus(status, pageable);

        } else if(priority != null) {
            page = ticketRepository.findByPriority(priority, pageable);

        } else if(customerId != null) {
            page = ticketRepository.findByCustomerId(customerId, pageable);
        } else {
            page = ticketRepository.findAll(pageable);
        }

        return page.map(this::toResponse);
    }

    public TicketResponse getTicket(Long id) {

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));
        return toResponse(ticket);
    }

    @Transactional
    public TicketResponse updateStatus(Long ticketId, Ticket.TicketStatus newStatus) {

        Ticket ticket  =  ticketRepository.findById(ticketId)
                .orElseThrow( () -> new ResourceNotFoundException(
                        "Ticket not found with id: " + ticketId
                ));
        ticket.setStatus(newStatus);

        if (newStatus == Ticket.TicketStatus.RESOLVED || newStatus == Ticket.TicketStatus.CLOSED) {

            ticket.setResolvedAt(java.time.LocalDateTime.now());
        }

        Ticket updated = ticketRepository.save(ticket);
        return toResponse(updated);
    }

    @Transactional
    public TicketResponse assignAgent(Long ticketId, Long agentId) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow( () -> new ResourceNotFoundException(
                        "Ticket not found with id: " + ticketId
                ));

        User agent = userRepository.findById(agentId)
                .orElseThrow( () -> new ResourceNotFoundException(
                        "Agent not found with id: " + agentId
                ));

        if (agent.getRole() != User.Role.AGENT && agent.getRole() != User.Role.ADMIN) {

            throw new IllegalArgumentException("User is an agent or admin");
        }

        ticket.setAssignedAgent(agent);

        if (ticket.getStatus() == Ticket.TicketStatus.OPEN) {

            ticket.setStatus(Ticket.TicketStatus.IN_PROGRESS);
        }

        Ticket updated = ticketRepository.save(ticket);
        return toResponse(updated);
    }

    @Transactional
    public CommentResponse addComment(Long ticketId, AddCommentRequest request) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ticket not found with id: " + ticketId));

        User author = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Author not found with id: " + request.getAuthorId()));

        TicketComment comment = TicketComment.builder()
                .ticket(ticket)
                .author(author)
                .commentText(request.getCommentText())
                .build();

        TicketComment saved = ticketCommentRepository.save(comment);
        return toCommentResponse(saved);
    }
    public List<CommentResponse> getComments(Long ticketId) {
        if (!ticketRepository.existsById(ticketId)) {
            throw new ResourceNotFoundException("Ticket not found with id: " + ticketId);
        }
        return ticketCommentRepository.findByTicketIdOrderByCreatedAtAsc(ticketId)
                .stream()// we are using steam()
                .map(this::toCommentResponse)
                .toList();
    }

    private CommentResponse toCommentResponse(TicketComment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .ticketId(comment.getTicket().getId())
                .authorId(comment.getAuthor().getId())
                .authorName(comment.getAuthor().getFullName())
                .commentText(comment.getCommentText())
                .createdAt(comment.getCreatedAt())
                .build();
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
