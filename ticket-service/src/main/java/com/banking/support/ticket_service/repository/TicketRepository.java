package com.banking.support.ticket_service.repository;

import com.banking.support.ticket_service.entity.Ticket;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

@Repository
public interface TicketRepository  extends JpaRepository<Ticket, Long> {



    List<Ticket> findByCustomerId(Long customerId);

    List<Ticket> findByStatus(Ticket.TicketStatus status);

    List<Ticket> findByAssignedAgentId(Long agentId);

    Page<Ticket> findByStatus(Ticket.TicketStatus status, Pageable pageable);

    Page<Ticket> findByCustomerId(Long customerId, Pageable pageable);

    Page<Ticket> findByPriority(Ticket.Priority priority, Pageable pageable);


}
