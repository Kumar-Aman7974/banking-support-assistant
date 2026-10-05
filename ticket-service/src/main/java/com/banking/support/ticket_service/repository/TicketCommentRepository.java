package com.banking.support.ticket_service.repository;

import com.banking.support.ticket_service.entity.TicketComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;

public interface TicketCommentRepository extends JpaRepository<TicketComment, Long> {



    List<TicketComment> findByTicketIdOrderByCreatedAtAsc(Long ticketId);
}
