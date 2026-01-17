package com.prod.backend.customersupport.ticket;

import com.prod.backend.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class TicketController {

    private final TicketService ticketService;

    @PostMapping("/tickets")
    public ResponseEntity<TicketResponse> createTicket(@RequestBody CreateTicketRequest createTicketRequest, @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(ticketService.createTicket(createTicketRequest, user.getId()));
    }

    @GetMapping("/tickets")
    public ResponseEntity<List<TicketResponse>> getTickets(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(ticketService.getTickets(user.getId()));
    }
}
