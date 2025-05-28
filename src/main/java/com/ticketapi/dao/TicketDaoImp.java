package com.ticketapi.dao;

import com.ticketapi.model.Ticket;
import com.ticketapi.util.DatabaseQueries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TicketDaoImp implements TicketDao {

    private final JdbcTemplate jdbcTemplate;
    private static final Logger logger = LoggerFactory.getLogger(TicketDaoImp.class);

    public TicketDaoImp(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Ticket createTicket(Ticket ticket) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(DatabaseQueries.CREATE_TICKET.getQuery(), new String[]{"id"});
                ps.setLong(1, ticket.getEventId());
                ps.setLong(2, ticket.getUserId());
                ps.setString(3, ticket.getTicketType());
                ps.setDouble(4, ticket.getPrice());
                return ps;
            }, keyHolder);
            if (keyHolder.getKey() != null) {
                ticket.setId(keyHolder.getKey().longValue());
                return ticket;
            }
        } catch (DataAccessException e) {
            logger.error("Failed to create ticket for event: {} and user: {}", ticket.getEventId(), ticket.getUserId(), e);
        }
        return ticket;
    }

    @Override
    public Ticket getTicketById(Long id) {
        if (id == null || id <= 0) {
            logger.warn("Invalid ticket ID provided: {}", id);
            return null;
        }
        
        try {
            return jdbcTemplate.queryForObject(DatabaseQueries.GET_TICKET_BY_ID.getQuery(), new Object[]{id}, this::mapRowToTicket);
        } catch (EmptyResultDataAccessException e) {
            logger.debug("Ticket not found with ID: {}", id);
            return null;
        } catch (DataAccessException e) {
            logger.error("Error fetching ticket with ID: {}", id, e);
            throw new RuntimeException("Failed to fetch ticket with ID: " + id, e);
        }
    }

    @Override
    public List<Ticket> getAllTickets() {
        try {
            return jdbcTemplate.query(DatabaseQueries.GET_ALL_TICKETS.getQuery(), this::mapRowToTicket);
        } catch (DataAccessException e) {
            logger.error("Exception fetching all tickets", e);
        }
        return new ArrayList<>();
    }

    @Override
    public Void updateTicket(Ticket ticket) {
        if (ticket == null || ticket.getId() == null || ticket.getId() <= 0) {
            logger.warn("Invalid ticket provided for update: {}", ticket);
            throw new IllegalArgumentException("Ticket and ticket ID must be provided");
        }
        
        try {
            int rowsAffected = jdbcTemplate.update(DatabaseQueries.UPDATE_TICKET.getQuery(),
                    ticket.getEventId(), ticket.getUserId(), ticket.getTicketType(), ticket.getPrice(), ticket.getId());
            if (rowsAffected == 0) {
                logger.warn("No ticket updated with ID: {}", ticket.getId());
            }
        } catch (DataAccessException e) {
            logger.error("Error updating ticket with ID: {}", ticket.getId(), e);
            throw new RuntimeException("Failed to update ticket with ID: " + ticket.getId(), e);
        }
        return null;
    }

    @Override
    public Void deleteTicket(Long id) {
        if (id == null || id <= 0) {
            logger.warn("Invalid ticket ID provided for deletion: {}", id);
            throw new IllegalArgumentException("Valid ticket ID must be provided");
        }
        
        try {
            int rowsAffected = jdbcTemplate.update(DatabaseQueries.DELETE_TICKET.getQuery(), id);
            if (rowsAffected == 0) {
                logger.warn("No ticket deleted with ID: {}", id);
            }
        } catch (DataAccessException e) {
            logger.error("Error deleting ticket with ID: {}", id, e);
            throw new RuntimeException("Failed to delete ticket with ID: " + id, e);
        }
        return null;
    }

    @Override
    public List<Ticket> getTicketByUserName(String username) {
        if (username == null || username.trim().isEmpty()) {
            logger.warn("Invalid username provided: {}", username);
            return new ArrayList<>();
        }
        
        try {
            return jdbcTemplate.query(DatabaseQueries.GET_TICKET_BY_USERNAME.getQuery(), new Object[]{username.trim()}, this::mapRowToTicket);
        } catch (DataAccessException e) {
            logger.error("Error fetching tickets for user: {}", username, e);
            throw new RuntimeException("Failed to fetch tickets for user: " + username, e);
        }
    }

    private Ticket mapRowToTicket(ResultSet rs, int rowNum) throws SQLException {
        Ticket ticket = new Ticket();
        ticket.setId(rs.getLong("id"));
        ticket.setEventId(rs.getLong("event_id"));
        ticket.setUserId(rs.getLong("user_id"));
        ticket.setTicketType(rs.getString("ticket_type"));
        ticket.setPrice(rs.getDouble("price"));
        return ticket;
    }
}