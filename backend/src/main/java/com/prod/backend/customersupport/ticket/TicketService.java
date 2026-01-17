package com.prod.backend.customersupport.ticket;

import com.prod.backend.user.UserEntity;
import com.prod.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public TicketResponse createTicket(CreateTicketRequest createTicketRequest, Long loggedInUserId) {
        UserEntity user = userRepository.findById(loggedInUserId).orElseThrow(() -> new RuntimeException("User not found"));
        TicketEntity ticketEntity = TicketMapper.toEntity(createTicketRequest, user);
        TicketEntity saved = ticketRepository.save(ticketEntity);
        return TicketMapper.toDto(saved);
    }

    public List<TicketResponse> getTickets(Long loggedInUserId) {
        List<TicketEntity> tickets = ticketRepository.findByUser_Id(loggedInUserId);
        return tickets.stream().map(TicketMapper::toDto).collect(Collectors.toList());
    }

    public List<TicketResponseForAdmin> getAllTickets() {
        List<TicketEntity> tickets = ticketRepository.findAll();
        return tickets.stream().map(TicketMapper::toDtoForAdmin).collect(Collectors.toList());
    }
}
