package com.ticketapi.dao;

import com.ticketapi.model.Event;

import java.util.List;

public interface EventDao {
    Event createEvent(Event event);
    Event getEventById(Long id);
    List<Event> getAllEvents();
    void updateEvent(Event event);
    void deleteEvent(Long id);

}
