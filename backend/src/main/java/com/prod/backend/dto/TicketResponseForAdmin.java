package com.prod.backend.dto;

import com.prod.backend.enums.Status;
import lombok.Getter;
import lombok.Setter;

public record TicketResponseForAdmin(
        String userEmail,
        String subject,
        Status status,
        String description,
        String createdAt,
        String updatedAt
) {}
