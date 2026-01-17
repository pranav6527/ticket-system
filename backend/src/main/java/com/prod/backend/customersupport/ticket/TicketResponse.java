package com.prod.backend.customersupport.ticket;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketResponse {
    private String subject;
    private Status status;
    private String description;
}
