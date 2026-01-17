package com.prod.backend.customersupport.ticket;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTicketRequest {
    private String subject;
    private String description;
}
