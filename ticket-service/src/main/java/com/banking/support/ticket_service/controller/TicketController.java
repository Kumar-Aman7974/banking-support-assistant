package com.banking.support.ticket_service.controller;


import com.banking.support.ticket_service.dto.CreateTicketRequest;
import com.banking.support.ticket_service.dto.TicketResponse;
import com.banking.support.ticket_service.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public  ResponseEntity<List<TicketResponse>> getAllTickets() {

        return ResponseEntity.ok(ticketService.getAllTickets());

    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getTicket(@PathVariable Long id) {

        return  ResponseEntity.ok(ticketService.getTicket(id));
    }
}
