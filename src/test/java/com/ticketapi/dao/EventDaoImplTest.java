package com.ticketapi.dao;

import com.ticketapi.model.Event;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EventDaoImplTest {

    @Autowired
    private EventDao eventDao;
    
    private Event testEvent;

    @BeforeEach
    void setUp() {
        testEvent = new Event();
        testEvent.setName("Test Concert");
        testEvent.setDescription("A test concert event");
        testEvent.setDateTime(LocalDateTime.now().plusDays(30));
        testEvent.setVenue("Test Venue");
        testEvent.setTotalTickets(100);
        testEvent.setAvailableTickets(100);
        testEvent.setPrice(BigDecimal.valueOf(25.00));
    }

    @Test
    void testCreateEvent() {
        Event createdEvent = eventDao.createEvent(testEvent);

        assertNotNull(createdEvent.getId());
        assertTrue(createdEvent.getId() > 0);
        assertEquals("Test Concert", createdEvent.getName());
        assertEquals("A test concert event", createdEvent.getDescription());
        assertEquals("Test Venue", createdEvent.getVenue());
        assertEquals(100, createdEvent.getTotalTickets());
        assertEquals(100, createdEvent.getAvailableTickets());
        assertEquals(BigDecimal.valueOf(25.00), createdEvent.getPrice());
    }
    
    @Test
    void testGetEventById() {
        Event createdEvent = eventDao.createEvent(testEvent);
        Long eventId = createdEvent.getId();
        
        Event foundEvent = eventDao.getEventById(eventId);
        
        assertNotNull(foundEvent);
        assertEquals(eventId, foundEvent.getId());
        assertEquals("Test Concert", foundEvent.getName());
        assertEquals("Test Venue", foundEvent.getVenue());
    }
    
    @Test
    void testGetEventById_NotFound() {
        Event foundEvent = eventDao.getEventById(999L);
        assertNull(foundEvent);
    }
    
    @Test
    void testGetEventById_InvalidId() {
        assertNull(eventDao.getEventById(null));
        assertNull(eventDao.getEventById(0L));
        assertNull(eventDao.getEventById(-1L));
    }
    
    @Test
    void testGetAllEvents() {
        eventDao.createEvent(testEvent);
        
        Event secondEvent = new Event();
        secondEvent.setName("Second Event");
        secondEvent.setDescription("Second test event");
        secondEvent.setDateTime(LocalDateTime.now().plusDays(60));
        secondEvent.setVenue("Second Venue");
        secondEvent.setTotalTickets(50);
        secondEvent.setAvailableTickets(50);
        secondEvent.setPrice(BigDecimal.valueOf(0.00)); // Free event
        eventDao.createEvent(secondEvent);
        
        List<Event> events = eventDao.getAllEvents();
        
        assertNotNull(events);
        assertTrue(events.size() >= 2);
    }
    
    @Test
    void testUpdateEvent() {
        Event createdEvent = eventDao.createEvent(testEvent);
        
        createdEvent.setName("Updated Concert");
        createdEvent.setDescription("Updated description");
        createdEvent.setVenue("Updated Venue");
        createdEvent.setTotalTickets(200);
        createdEvent.setAvailableTickets(150);
        createdEvent.setPrice(BigDecimal.valueOf(35.00));
        
        assertDoesNotThrow(() -> eventDao.updateEvent(createdEvent));
        
        Event updatedEvent = eventDao.getEventById(createdEvent.getId());
        assertEquals("Updated Concert", updatedEvent.getName());
        assertEquals("Updated description", updatedEvent.getDescription());
        assertEquals("Updated Venue", updatedEvent.getVenue());
        assertEquals(200, updatedEvent.getTotalTickets());
        assertEquals(150, updatedEvent.getAvailableTickets());
        assertEquals(BigDecimal.valueOf(35.00), updatedEvent.getPrice());
    }
    
    @Test
    void testUpdateEvent_InvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> eventDao.updateEvent(null));
        
        Event eventWithoutId = new Event();
        eventWithoutId.setName("Test");
        assertThrows(IllegalArgumentException.class, () -> eventDao.updateEvent(eventWithoutId));
    }
    
    @Test
    void testDeleteEvent() {
        Event createdEvent = eventDao.createEvent(testEvent);
        Long eventId = createdEvent.getId();
        
        assertDoesNotThrow(() -> eventDao.deleteEvent(eventId));
        
        Event deletedEvent = eventDao.getEventById(eventId);
        assertNull(deletedEvent);
    }
    
    @Test
    void testDeleteEvent_InvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> eventDao.deleteEvent(null));
        assertThrows(IllegalArgumentException.class, () -> eventDao.deleteEvent(0L));
        assertThrows(IllegalArgumentException.class, () -> eventDao.deleteEvent(-1L));
    }
}
