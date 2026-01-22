package com.prod.backend.service;

import com.prod.backend.dto.CreateTicketRequest;
import com.prod.backend.dto.TicketResponse;
import com.prod.backend.dto.TicketResponseForAdmin;
import com.prod.backend.entity.TicketEntity;
import com.prod.backend.entity.UserEntity;
import com.prod.backend.mapper.TicketMapper;
import com.prod.backend.repo.TicketRepository;
import com.prod.backend.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
        List<TicketEntity> tickets = ticketRepository.findByUser_Id(loggedInUserId);
        return tickets.stream().map(ticketMapper::toDto).collect(Collectors.toList());
    }

    public List<TicketResponseForAdmin> getAllTickets() {
        List<TicketEntity> tickets = ticketRepository.findAll();
        return tickets.stream().map(ticketMapper::toDtoForAdmin).collect(Collectors.toList());
    }
}
