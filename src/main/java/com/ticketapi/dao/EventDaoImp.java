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
        try{
        return jdbcTemplate
                .queryForObject(DatabaseQueries.GET_EVENT_BY_ID.getQuery(),new Object[]{id}, this::mapRowToEvent);
        } catch (EmptyResultDataAccessException e){
            logger.error("event not found with id: {}", id, e);
        }catch (DataAccessException e) {
            logger.error("Exception fetching event with id: {}", id, e);
        }
        return new Event();
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
        try{
            jdbcTemplate.update(DatabaseQueries.UPDATE_EVENT.getQuery(),event.getName(), event.getDescription(), Timestamp.valueOf(event.getDateTime()),
                    event.getVenue(), event.getTotalTickets(), event.getAvailableTickets(), event.getId());
        } catch (DataAccessException e) {
            logger.error("Exception updating event with name: {}", event.getName(), e);
        }

    }

    @Override
    public void deleteEvent(Long id) {
        try{
            jdbcTemplate.update(DatabaseQueries.DELETE_EVENT.getQuery(), id);
        } catch (DataAccessException e) {
            logger.error("Error deleting event with id: {}", id, e);
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
        return event;
    }
}
