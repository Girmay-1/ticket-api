package com.ticketapi.service;
import com.ticketapi.dao.TicketDao;
import com.ticketapi.model.Ticket;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {
    private final TicketDao ticketDao;

    public TicketService(TicketDao ticketDao) {
        this.ticketDao = ticketDao;
    }

    public Ticket createTicket(Ticket ticket) {
        return ticketDao.createTicket(ticket);
    }

    public Ticket getTicketById(Long id) {
        return ticketDao.getTicketById(id);
    }

    public List<Ticket> getAllTickets() {
        return ticketDao.getAllTickets();
    }

    public void updateTicket(Ticket ticket) {
        ticketDao.updateTicket(ticket);
    }

    public void deleteTicket(Long id) {
        ticketDao.deleteTicket(id);
    }

    public List<Ticket> getTicketsByUsername(String username) {
         return ticketDao.getTicketByUserName(username);
    }
}