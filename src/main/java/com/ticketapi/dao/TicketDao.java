package com.ticketapi.dao;

import com.ticketapi.model.Ticket;

import java.util.List;

public interface TicketDao {
    Ticket createTicket(Ticket ticket);
    Ticket getTicketById(Long id);
    List<Ticket> getAllTickets();
    Void updateTicket(Ticket ticket);
    Void deleteTicket(Long id);

    List<Ticket> getTicketByUserName(String username);
}
