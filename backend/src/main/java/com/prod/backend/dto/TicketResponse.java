package com.prod.backend.dto;

import com.prod.backend.enums.Status;

public record TicketResponse(
        int id,
        String subject,
        Status status,
        String description
) {}
