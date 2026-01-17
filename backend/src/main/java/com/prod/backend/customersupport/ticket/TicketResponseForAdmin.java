package com.prod.backend.customersupport.ticket;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketResponseForAdmin {
    private String userEmail;
    private String subject;
    private Status status;
    private String description;
    private String createdAt;
    private String updatedAt;
}
