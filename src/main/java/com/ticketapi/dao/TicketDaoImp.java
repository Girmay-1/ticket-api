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
        try {
            return jdbcTemplate.queryForObject(DatabaseQueries.GET_TICKET_BY_ID.getQuery(), new Object[]{id}, this::mapRowToTicket);
        } catch (EmptyResultDataAccessException e) {
            logger.error("Ticket not found with id: {}", id, e);
        } catch (DataAccessException e) {
            logger.error("Exception fetching ticket with id: {}", id, e);
        }
        return null;
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
        try {
            jdbcTemplate.update(DatabaseQueries.UPDATE_TICKET.getQuery(),
                    ticket.getEventId(), ticket.getUserId(), ticket.getTicketType(), ticket.getPrice(), ticket.getId());
        } catch (DataAccessException e) {
            logger.error("Exception updating ticket with id: {}", ticket.getId(), e);
        }
        return null;
    }

    @Override
    public Void deleteTicket(Long id) {
        try {
            jdbcTemplate.update(DatabaseQueries.DELETE_TICKET.getQuery(), id);
        } catch (DataAccessException e) {
            logger.error("Error deleting ticket with id: {}", id, e);
        }
        return null;
    }

    @Override
    public List<Ticket> getTicketByUserName(String username) {
        try{
            return jdbcTemplate.query(DatabaseQueries.GET_TICKET_BY_USERNAME.getQuery(), new Object[]{username}, this::mapRowToTicket);
        } catch (Exception e) {
            logger.error("exception when trying to get tickets for user: {}", username, e);
        }
        return new ArrayList<>();
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