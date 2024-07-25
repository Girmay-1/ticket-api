package com.ticketapi.service;

import com.ticketapi.dao.EventDao;
import com.ticketapi.model.Event;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {
    private final EventDao eventDao;

    public EventService(EventDao eventDao) {
        this.eventDao = eventDao;
    }

    public Event createEvent(Event event) {
        return eventDao.createEvent(event);
    }

    public Event getEventById(Long id) {
        return eventDao.getEventById(id);
    }

    public List<Event> getAllEvents() {
        return eventDao.getAllEvents();
    }

    public void updateEvent(Event event) {
        eventDao.updateEvent(event);
    }

    public void deleteEvent(Long id) {
        eventDao.deleteEvent(id);
    }
}



