package com.prod.backend.dto;

import com.prod.backend.enums.Status;
import lombok.Getter;
import lombok.Setter;

public record TicketResponse(String subject, Status status, String description) {}
