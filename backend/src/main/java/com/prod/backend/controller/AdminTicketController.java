package com.prod.backend.controller;

import com.prod.backend.dto.TicketResponseForAdmin;
import com.prod.backend.dto.UpdateTicketRequest;
import com.prod.backend.service.TicketService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@SecurityRequirement(name = "bearerAuth")
public class AdminTicketController {

    private final TicketService ticketService;

    @GetMapping("/tickets")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TicketResponseForAdmin>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @PutMapping("/tickets/{ticketId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TicketResponseForAdmin> updateTicket(
            @PathVariable int ticketId,
            @RequestBody UpdateTicketRequest updateTicketRequest
    ) {
        return ResponseEntity.ok(ticketService.updateTicketAsAdmin(ticketId, updateTicketRequest));
    }
}
