package com.banking.support.ticket_service;

import com.banking.support.ticket_service.dto.CreateTicketRequest;
import com.banking.support.ticket_service.dto.SignupRequest;
import com.banking.support.ticket_service.entity.Ticket;
import com.banking.support.ticket_service.entity.User;
import com.banking.support.ticket_service.repository.TicketCommentRepository;
import com.banking.support.ticket_service.repository.TicketRepository;
import com.banking.support.ticket_service.repository.UserRepository;
import com.netflix.discovery.converters.Auto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TicketIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketCommentRepository commentRepository;

    @Autowired
    private TicketRepository ticketRepository;

    private String customerToken;

    @BeforeEach
    void setup() throws Exception {
        commentRepository.deleteAll();   // ← add this
        ticketRepository.deleteAll();
        userRepository.deleteAll();

        SignupRequest signup = new SignupRequest();
        signup.setEmail("customer@ticket.com");
        signup.setPassword("password123");
        signup.setFullName("Ticket Customer");
        signup.setRole(User.Role.CUSTOMER);

        String response = mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signup)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        customerToken = objectMapper.readTree(response).get("token").asText();
    }

    @Test
    void shouldCreateTicketAsAuthenticatedCustomer() throws Exception {
        CreateTicketRequest request = new CreateTicketRequest();
        request.setSubject("Test Subject");
        request.setDescription("Test Description");
        request.setPriority(Ticket.Priority.HIGH);

        mockMvc.perform(post("/api/v1/tickets")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.subject").value("Test Subject"))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.priority").value("HIGH"));
    }

    @Test
    void shouldRejectCreateWithoutAuth() throws Exception {
        CreateTicketRequest request = new CreateTicketRequest();
        request.setSubject("Test");
        request.setDescription("Test");
        request.setPriority(Ticket.Priority.LOW);

        mockMvc.perform(post("/api/v1/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldRejectInvalidTicketPayload() throws Exception {
        CreateTicketRequest request = new CreateTicketRequest();
        // subject is blank - should fail validation

        mockMvc.perform(post("/api/v1/tickets")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void shouldGetAllTicketsAsAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/api/v1/tickets")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void shouldReturn404ForNonExistentTicket() throws Exception {
        mockMvc.perform(get("/api/v1/tickets/99999")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }
}