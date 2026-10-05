package com.banking.support.ticket_service.controller;


import com.banking.support.ticket_service.dto.*;
import com.banking.support.ticket_service.entity.Ticket;
import com.banking.support.ticket_service.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(@Valid @RequestBody CreateTicketRequest request) {

        TicketResponse response = ticketService.createTicket(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @GetMapping
    public ResponseEntity<Page<TicketResponse>> getAllTickets(
            @RequestParam(required = false) Ticket.TicketStatus status,
            @RequestParam(required = false) Ticket.Priority priority,
            @RequestParam(required = false) Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        String[] sortParts = sort.split(",");
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Direction.fromString(sortParts[1]),
                        sortParts[0]
                )
        );

        return ResponseEntity.ok(
                ticketService.getAllTickets(status, priority, customerId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getTicket(@PathVariable Long id) {

        return  ResponseEntity.ok(ticketService.getTicket(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TicketResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTicketStatusRequest request) {
        return ResponseEntity.ok(ticketService.updateStatus(id, request.getStatus()));
    }

    @PatchMapping("/{id}/assign")
    public ResponseEntity<TicketResponse> assignAgent(
            @PathVariable Long id,
            @Valid @RequestBody AssignAgentRequest request) {
        return ResponseEntity.ok(ticketService.assignAgent(id, request.getAgentId()));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long id,
            @Valid @RequestBody AddCommentRequest request) {
        CommentResponse response = ticketService.addComment(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.getComments(id));
    }

}
