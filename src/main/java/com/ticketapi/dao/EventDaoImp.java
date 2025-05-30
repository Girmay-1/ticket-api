package com.ticketapi.dao;

import com.ticketapi.model.Event;
import com.ticketapi.util.DatabaseQueries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EventDaoImp implements EventDao{

    private final JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(EventDaoImp.class);

    public EventDaoImp(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Event createEvent(Event event) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {

            jdbcTemplate.update(connection -> {
                        PreparedStatement ps = connection.prepareStatement(DatabaseQueries.CREATE_EVENT.getQuery(), new String[]{"id"});
                        ps.setString(1, event.getName());
                        ps.setString(2, event.getDescription());
                        ps.setTimestamp(3, Timestamp.valueOf(event.getDateTime()));
                        ps.setString(4, event.getVenue());
                        ps.setInt(5, event.getTotalTickets());
                        ps.setInt(6, event.getAvailableTickets());
                        ps.setBigDecimal(7, event.getPrice() != null ? event.getPrice() : BigDecimal.ZERO);
                        return ps;
                    }, keyHolder
            );
            if(keyHolder.getKey() != null){
                event.setId(keyHolder.getKey().longValue());
                return event;
            }
        } catch (DataAccessException e) {
            logger.error("failed to create event: {} with exception:", event.getName(), e);
        }
        return event;
    }

    @Override
    public Event getEventById(Long id) {
        if (id == null || id <= 0) {
            logger.warn("Invalid event ID provided: {}", id);
            return null;
        }
        
        try {
            return jdbcTemplate.queryForObject(
                    DatabaseQueries.GET_EVENT_BY_ID.getQuery(), 
                    new Object[]{id}, 
                    this::mapRowToEvent
            );
        } catch (EmptyResultDataAccessException e) {
            logger.debug("Event not found with ID: {}", id);
            return null;
        } catch (DataAccessException e) {
            logger.error("Error fetching event with ID: {}", id, e);
            throw new RuntimeException("Failed to fetch event with ID: " + id, e);
        }
    }



    @Override
    public List<Event> getAllEvents() {
        try{
            return jdbcTemplate
                    .query(DatabaseQueries.GET_ALL_EVENTS.getQuery(), this::mapRowToEvent);

        } catch (DataAccessException e) {
            logger.error("exception fetching all events:", e);
        }
        return new ArrayList<>();
    }

    @Override
    public void updateEvent(Event event) {
        if (event == null || event.getId() == null || event.getId() <= 0) {
            logger.warn("Invalid event provided for update: {}", event);
            throw new IllegalArgumentException("Event and event ID must be provided");
        }
        
        try {
            int rowsAffected = jdbcTemplate.update(
                    DatabaseQueries.UPDATE_EVENT.getQuery(),
                    event.getName(), 
                    event.getDescription(), 
                    Timestamp.valueOf(event.getDateTime()),
                    event.getVenue(), 
                    event.getTotalTickets(), 
                    event.getAvailableTickets(),
                    event.getPrice() != null ? event.getPrice() : BigDecimal.ZERO,
                    event.getId()
            );
            if (rowsAffected == 0) {
                logger.warn("No event updated with ID: {}", event.getId());
            }
        } catch (DataAccessException e) {
            logger.error("Error updating event with ID: {}", event.getId(), e);
            throw new RuntimeException("Failed to update event with ID: " + event.getId(), e);
        }
    }

    @Override
    public void deleteEvent(Long id) {
        if (id == null || id <= 0) {
            logger.warn("Invalid event ID provided for deletion: {}", id);
            throw new IllegalArgumentException("Valid event ID must be provided");
        }
        
        try {
            int rowsAffected = jdbcTemplate.update(DatabaseQueries.DELETE_EVENT.getQuery(), id);
            if (rowsAffected == 0) {
                logger.warn("No event deleted with ID: {}", id);
            }
        } catch (DataAccessException e) {
            logger.error("Error deleting event with ID: {}", id, e);
            throw new RuntimeException("Failed to delete event with ID: " + id, e);
        }
    }
    private Event mapRowToEvent(ResultSet rs, int rowNum) throws SQLException {
        Event event = new Event();
        event.setId(rs.getLong("id"));
        event.setName(rs.getString("name"));
        event.setDescription(rs.getString("description"));
        event.setDateTime(rs.getTimestamp("date_time").toLocalDateTime());
        event.setVenue(rs.getString("venue"));
        event.setTotalTickets(rs.getInt("total_tickets"));
        event.setAvailableTickets(rs.getInt("available_tickets"));
        event.setPrice(rs.getBigDecimal("price") != null ? rs.getBigDecimal("price") : BigDecimal.ZERO);
        return event;
    }
}
