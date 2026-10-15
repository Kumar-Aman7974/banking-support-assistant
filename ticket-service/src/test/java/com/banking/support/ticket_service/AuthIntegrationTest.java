package com.banking.support.ticket_service;

import com.banking.support.ticket_service.dto.LoginRequest;
import com.banking.support.ticket_service.dto.SignupRequest;
import com.banking.support.ticket_service.dto.TicketResponse;
import com.banking.support.ticket_service.entity.User;
import com.banking.support.ticket_service.repository.TicketCommentRepository;
import com.banking.support.ticket_service.repository.TicketRepository;
import com.banking.support.ticket_service.repository.UserRepository;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthIntegrationTest   extends BaseIntegrationTest{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketCommentRepository commentRepository;

    @BeforeEach
    void cleanup() {
        commentRepository.deleteAll();
        ticketRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldSignupNewCustomerSuccessfully() throws Exception {

        SignupRequest request = new SignupRequest();

        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setFullName("Test User");
        request.setRole(User.Role.CUSTOMER);

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));

    }

    @Test
    void shouldRejectDuplicateEmail()  throws Exception {

        SignupRequest request = new SignupRequest();

        request.setEmail("dup@example.com");
        request.setPassword("password123");
        request.setFullName("Dup User");
        request.setRole(User.Role.CUSTOMER);

        //First signup
        mockMvc.perform(post("/api/v1/auth/signup")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        //Second signup should fail
        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());



    }


    @Test
    void shouldLoginWithValidCredentials() throws Exception {
        SignupRequest signup = new SignupRequest();
        signup.setEmail("login@example.com");
        signup.setPassword("password123");
        signup.setFullName("Login User");
        signup.setRole(User.Role.CUSTOMER);

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signup)))
                .andExpect(status().isCreated());

        LoginRequest login = new LoginRequest();
        login.setEmail("login@example.com");
        login.setPassword("password123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void shouldRejectInvalidPassword() throws Exception {
        SignupRequest signup = new SignupRequest();
        signup.setEmail("wrongpass@example.com");
        signup.setPassword("password123");
        signup.setFullName("User");
        signup.setRole(User.Role.CUSTOMER);

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signup)))
                .andExpect(status().isCreated());

        LoginRequest login = new LoginRequest();
        login.setEmail("wrongpass@example.com");
        login.setPassword("wrongpassword");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().is5xxServerError());
    }

}
