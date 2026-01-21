package com.prod.backend.dto;

import com.prod.backend.enums.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketResponse {
    private String subject;
    private Status status;
    private String description;
}
