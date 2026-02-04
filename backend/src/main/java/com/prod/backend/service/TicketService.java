package com.prod.backend.service;

import com.prod.backend.dto.CreateTicketRequest;
import com.prod.backend.dto.TicketResponse;
import com.prod.backend.dto.TicketResponseForAdmin;
import com.prod.backend.dto.UpdateTicketRequest;
import com.prod.backend.entity.TicketEntity;
import com.prod.backend.entity.UserEntity;
import com.prod.backend.mapper.TicketMapper;
import com.prod.backend.repo.TicketRepository;
import com.prod.backend.repo.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final TicketMapper ticketMapper;

    public TicketResponse createTicket(CreateTicketRequest createTicketRequest, Long loggedInUserId) {
        UserEntity user = userRepository.findById(loggedInUserId).orElseThrow(() -> new RuntimeException("User not found"));
        TicketEntity ticketEntity = ticketMapper.toEntity(createTicketRequest, user);
        TicketEntity saved = ticketRepository.save(ticketEntity);
        return ticketMapper.toDto(saved);
    }

    public List<TicketResponse> getTickets(Long loggedInUserId) {
        List<TicketEntity> tickets = ticketRepository.findByUser_IdOrderByCreatedAtDesc(loggedInUserId);
        return tickets.stream().map(ticketMapper::toDto).collect(Collectors.toList());
    }

    public List<TicketResponseForAdmin> getAllTickets() {
        List<TicketEntity> tickets = ticketRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        return tickets.stream().map(ticketMapper::toDtoForAdmin).collect(Collectors.toList());
    }

    @Transactional
    public TicketResponseForAdmin updateTicketAsAdmin(int ticketId, UpdateTicketRequest updateRequest) {
        TicketEntity ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        if (updateRequest.subject() != null) {
            ticket.setSubject(updateRequest.subject());
        }
        if (updateRequest.description() != null) {
            ticket.setDescription(updateRequest.description());
        }
        if (updateRequest.status() != null) {
            ticket.setStatus(updateRequest.status());
        }

        ticket.setUpdatedAt(LocalDateTime.now());
        TicketEntity saved = ticketRepository.save(ticket);
        return ticketMapper.toDtoForAdmin(saved);
    }
}
