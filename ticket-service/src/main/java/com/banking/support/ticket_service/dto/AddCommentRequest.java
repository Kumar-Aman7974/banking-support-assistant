package com.banking.support.ticket_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddCommentRequest {

    @NotNull(message = "Author ID is required")
    private Long authorId;

    @NotBlank(message = "Comment text is required")
    private String commentText;
}
