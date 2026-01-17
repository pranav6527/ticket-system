package com.prod.backend.customersupport.ticket;

import com.prod.backend.user.UserEntity;

import java.time.LocalDateTime;

public class TicketMapper {
    public static TicketEntity toEntity(CreateTicketRequest dto, UserEntity user) {
        TicketEntity ticketEntity = new TicketEntity();
        ticketEntity.setSubject(dto.getSubject());
        ticketEntity.setDescription(dto.getDescription());
        ticketEntity.setUser(user);
        ticketEntity.setCreatedAt(LocalDateTime.now());
        ticketEntity.setUpdatedAt(LocalDateTime.now());
        ticketEntity.setStatus(Status.OPEN);
        return ticketEntity;
    }

    public static TicketResponse toDto(TicketEntity ticketEntity) {
        TicketResponse ticketResponse = new TicketResponse();
        ticketResponse.setSubject(ticketEntity.getSubject());
        ticketResponse.setStatus(ticketEntity.getStatus());
        ticketResponse.setDescription(ticketEntity.getDescription());
        return ticketResponse;
    }

    public static TicketResponseForAdmin toDtoForAdmin(TicketEntity ticketEntity) {
        TicketResponseForAdmin ticketResponseForAdmin = new TicketResponseForAdmin();
        ticketResponseForAdmin.setUserEmail(ticketEntity.getUser().getEmail());
        ticketResponseForAdmin.setSubject(ticketEntity.getSubject());
        ticketResponseForAdmin.setStatus(ticketEntity.getStatus());
        ticketResponseForAdmin.setDescription(ticketEntity.getDescription());
        ticketResponseForAdmin.setCreatedAt(ticketEntity.getCreatedAt().toString());
        ticketResponseForAdmin.setUpdatedAt(ticketEntity.getUpdatedAt().toString());
        return ticketResponseForAdmin;
    }

}
