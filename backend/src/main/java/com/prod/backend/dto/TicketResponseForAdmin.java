package com.prod.backend.dto;

import com.prod.backend.enums.Status;

public record TicketResponseForAdmin(
        int id,
        String userEmail,
        String subject,
        Status status,
        String description,
        String createdAt,
        String updatedAt
) {}
