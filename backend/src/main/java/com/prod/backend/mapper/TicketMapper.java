package com.prod.backend.mapper;

import com.prod.backend.dto.CreateTicketRequest;
import com.prod.backend.dto.TicketResponse;
import com.prod.backend.dto.TicketResponseForAdmin;
import com.prod.backend.entity.TicketEntity;
import com.prod.backend.entity.UserEntity;
import com.prod.backend.enums.Status;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class TicketMapper {

    public TicketEntity toEntity(CreateTicketRequest dto, UserEntity user) {
        if (dto == null && user == null) {
            return null;
        }

        TicketEntity ticketEntity = new TicketEntity();

        if (dto != null) {
            ticketEntity.setSubject(dto.subject());
            ticketEntity.setDescription(dto.description());
        }

        ticketEntity.setUser(user);
        ticketEntity.setCreatedAt(LocalDateTime.now());
        ticketEntity.setUpdatedAt(LocalDateTime.now());
        ticketEntity.setStatus(Status.OPEN);

        return ticketEntity;
    }

    public TicketResponse toDto(TicketEntity ticketEntity) {
        if (ticketEntity == null) {
            return null;
        }

        return new TicketResponse(
                ticketEntity.getId(),
                ticketEntity.getSubject(),
                ticketEntity.getStatus(),
                ticketEntity.getDescription()
        );
    }

    public TicketResponseForAdmin toDtoForAdmin(TicketEntity ticketEntity) {
        if (ticketEntity == null) {
            return null;
        }

        String createdAt = null;
        if (ticketEntity.getCreatedAt() != null) {
            createdAt = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(ticketEntity.getCreatedAt());
        }

        String updatedAt = null;
        if (ticketEntity.getUpdatedAt() != null) {
            updatedAt = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(ticketEntity.getUpdatedAt());
        }

        String userEmail = null;
        UserEntity user = ticketEntity.getUser();
        if (user != null) {
            userEmail = user.getEmail();
        }

        return new TicketResponseForAdmin(
                ticketEntity.getId(),
                userEmail,
                ticketEntity.getSubject(),
                ticketEntity.getStatus(),
                ticketEntity.getDescription(),
                createdAt,
                updatedAt
        );
    }
}
