package com.prod.backend.mapper;

import com.prod.backend.dto.CreateTicketRequest;
import com.prod.backend.dto.TicketResponse;
import com.prod.backend.dto.TicketResponseForAdmin;
import com.prod.backend.entity.TicketEntity;
import com.prod.backend.entity.UserEntity;
import com.prod.backend.enums.Status;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class, Status.class})
public interface TicketMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subject", source = "dto.subject")
    @Mapping(target = "description", source = "dto.description")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())")
    @Mapping(target = "status", expression = "java(Status.OPEN)")
    TicketEntity toEntity(CreateTicketRequest dto, UserEntity user);

    // Basic Response mapping
    TicketResponse toDto(TicketEntity ticketEntity);

    // Admin Response mapping with nested data
    @Mapping(target = "userEmail", source = "user.email")
    TicketResponseForAdmin toDtoForAdmin(TicketEntity ticketEntity);
}