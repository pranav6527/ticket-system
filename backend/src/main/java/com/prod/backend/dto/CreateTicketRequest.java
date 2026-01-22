package com.prod.backend.dto;

import lombok.Getter;
import lombok.Setter;

public record CreateTicketRequest(String subject, String description) {}
