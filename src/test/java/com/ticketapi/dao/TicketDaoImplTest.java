package com.ticketapi.dao;

import com.ticketapi.model.Event;
import com.ticketapi.model.Ticket;
import com.ticketapi.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TicketDaoImplTest {

    @Autowired
    private TicketDao ticketDao;
    
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private EventDao eventDao;
    
    private Ticket testTicket;
    private User testUser;
    private Event testEvent;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("hashedPassword");
        testUser = userDao.createUser(testUser);
        
        // Create test event
        testEvent = new Event();
        testEvent.setName("Test Concert");
        testEvent.setDescription("A test concert event");
        testEvent.setDateTime(LocalDateTime.now().plusDays(30));
        testEvent.setVenue("Test Venue");
        testEvent.setTotalTickets(100);
        testEvent.setAvailableTickets(100);
        testEvent = eventDao.createEvent(testEvent);
        
        // Create test ticket
        testTicket = new Ticket();
        testTicket.setEventId(testEvent.getId());
        testTicket.setUserId(testUser.getId());
        testTicket.setTicketType("VIP");
        testTicket.setPrice(99.99);
    }

    @Test
    void testCreateTicket() {
        Ticket createdTicket = ticketDao.createTicket(testTicket);

        assertNotNull(createdTicket.getId());
        assertTrue(createdTicket.getId() > 0);
        assertEquals(testEvent.getId(), createdTicket.getEventId());
        assertEquals(testUser.getId(), createdTicket.getUserId());
        assertEquals("VIP", createdTicket.getTicketType());
        assertEquals(99.99, createdTicket.getPrice());
    }
    
    @Test
    void testGetTicketById() {
        Ticket createdTicket = ticketDao.createTicket(testTicket);
        Long ticketId = createdTicket.getId();
        
        Ticket foundTicket = ticketDao.getTicketById(ticketId);
        
        assertNotNull(foundTicket);
        assertEquals(ticketId, foundTicket.getId());
        assertEquals(testEvent.getId(), foundTicket.getEventId());
        assertEquals(testUser.getId(), foundTicket.getUserId());
        assertEquals("VIP", foundTicket.getTicketType());
    }
    
    @Test
    void testGetTicketById_NotFound() {
        Ticket foundTicket = ticketDao.getTicketById(999L);
        assertNull(foundTicket);
    }
    
    @Test
    void testGetTicketById_InvalidId() {
        assertNull(ticketDao.getTicketById(null));
        assertNull(ticketDao.getTicketById(0L));
        assertNull(ticketDao.getTicketById(-1L));
    }
    
    @Test
    void testGetAllTickets() {
        ticketDao.createTicket(testTicket);
        
        Ticket secondTicket = new Ticket();
        secondTicket.setEventId(testEvent.getId());
        secondTicket.setUserId(testUser.getId());
        secondTicket.setTicketType("Regular");
        secondTicket.setPrice(49.99);
        ticketDao.createTicket(secondTicket);
        
        List<Ticket> tickets = ticketDao.getAllTickets();
        
        assertNotNull(tickets);
        assertTrue(tickets.size() >= 2);
    }
    
    @Test
    void testGetTicketByUserName() {
        ticketDao.createTicket(testTicket);
        
        List<Ticket> userTickets = ticketDao.getTicketByUserName("testuser");
        
        assertNotNull(userTickets);
        assertFalse(userTickets.isEmpty());
        assertEquals(testEvent.getId(), userTickets.get(0).getEventId());
        assertEquals(testUser.getId(), userTickets.get(0).getUserId());
    }
    
    @Test
    void testGetTicketByUserName_NotFound() {
        List<Ticket> userTickets = ticketDao.getTicketByUserName("nonexistent");
        
        assertNotNull(userTickets);
        assertTrue(userTickets.isEmpty());
    }
    
    @Test
    void testGetTicketByUserName_InvalidInput() {
        List<Ticket> result1 = ticketDao.getTicketByUserName(null);
        List<Ticket> result2 = ticketDao.getTicketByUserName("");
        List<Ticket> result3 = ticketDao.getTicketByUserName("   ");
        
        assertNotNull(result1);
        assertNotNull(result2);
        assertNotNull(result3);
        assertTrue(result1.isEmpty());
        assertTrue(result2.isEmpty());
        assertTrue(result3.isEmpty());
    }
    
    @Test
    void testUpdateTicket() {
        Ticket createdTicket = ticketDao.createTicket(testTicket);
        
        createdTicket.setTicketType("Premium");
        createdTicket.setPrice(149.99);
        
        assertDoesNotThrow(() -> ticketDao.updateTicket(createdTicket));
        
        Ticket updatedTicket = ticketDao.getTicketById(createdTicket.getId());
        assertEquals("Premium", updatedTicket.getTicketType());
        assertEquals(149.99, updatedTicket.getPrice());
    }
    
    @Test
    void testUpdateTicket_InvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> ticketDao.updateTicket(null));
        
        Ticket ticketWithoutId = new Ticket();
        ticketWithoutId.setTicketType("Test");
        assertThrows(IllegalArgumentException.class, () -> ticketDao.updateTicket(ticketWithoutId));
    }
    
    @Test
    void testDeleteTicket() {
        Ticket createdTicket = ticketDao.createTicket(testTicket);
        Long ticketId = createdTicket.getId();
        
        assertDoesNotThrow(() -> ticketDao.deleteTicket(ticketId));
        
        Ticket deletedTicket = ticketDao.getTicketById(ticketId);
        assertNull(deletedTicket);
    }
    
    @Test
    void testDeleteTicket_InvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> ticketDao.deleteTicket(null));
        assertThrows(IllegalArgumentException.class, () -> ticketDao.deleteTicket(0L));
        assertThrows(IllegalArgumentException.class, () -> ticketDao.deleteTicket(-1L));
    }
}
