package com.prod.backend.dto;

import com.prod.backend.enums.Status;

public record UpdateTicketRequest(
        String subject,
        String description,
        Status status
) {}
