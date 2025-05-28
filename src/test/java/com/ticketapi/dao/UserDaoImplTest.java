package com.ticketapi.dao;

import com.ticketapi.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserDaoImplTest {

    @Autowired
    private UserDao userDao;
    
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("hashedPassword");
    }

    @Test
    void testCreateUser() {
        User createdUser = userDao.createUser(testUser);

        assertNotNull(createdUser.getId());
        assertTrue(createdUser.getId() > 0);
        assertEquals("testuser", createdUser.getUsername());
        assertEquals("test@example.com", createdUser.getEmail());
        assertEquals("hashedPassword", createdUser.getPassword());
    }
    
    @Test
    void testGetUserById() {
        User createdUser = userDao.createUser(testUser);
        Long userId = createdUser.getId();
        
        User foundUser = userDao.getUserById(userId);
        
        assertNotNull(foundUser);
        assertEquals(userId, foundUser.getId());
        assertEquals("testuser", foundUser.getUsername());
        assertEquals("test@example.com", foundUser.getEmail());
    }
    
    @Test
    void testGetUserById_NotFound() {
        User foundUser = userDao.getUserById(999L);
        assertNull(foundUser);
    }
    
    @Test
    void testGetUserById_InvalidId() {
        assertNull(userDao.getUserById(null));
        assertNull(userDao.getUserById(0L));
        assertNull(userDao.getUserById(-1L));
    }
    
    @Test
    void testGetUserByUsername() {
        userDao.createUser(testUser);
        
        User foundUser = userDao.getUserByUsername("testuser");
        
        assertNotNull(foundUser);
        assertEquals("testuser", foundUser.getUsername());
        assertEquals("test@example.com", foundUser.getEmail());
    }
    
    @Test
    void testGetUserByUsername_NotFound() {
        User foundUser = userDao.getUserByUsername("nonexistent");
        assertNull(foundUser);
    }
    
    @Test
    void testGetUserByUsername_InvalidInput() {
        assertNull(userDao.getUserByUsername(null));
        assertNull(userDao.getUserByUsername(""));
        assertNull(userDao.getUserByUsername("   "));
    }
    
    @Test
    void testUpdateUser() {
        User createdUser = userDao.createUser(testUser);
        
        createdUser.setUsername("updateduser");
        createdUser.setEmail("updated@example.com");
        
        assertDoesNotThrow(() -> userDao.updateUser(createdUser));
        
        User updatedUser = userDao.getUserById(createdUser.getId());
        assertEquals("updateduser", updatedUser.getUsername());
        assertEquals("updated@example.com", updatedUser.getEmail());
    }
    
    @Test
    void testUpdateUser_InvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> userDao.updateUser(null));
        
        User userWithoutId = new User();
        userWithoutId.setUsername("test");
        assertThrows(IllegalArgumentException.class, () -> userDao.updateUser(userWithoutId));
    }
    
    @Test
    void testDeleteUser() {
        User createdUser = userDao.createUser(testUser);
        Long userId = createdUser.getId();
        
        assertDoesNotThrow(() -> userDao.deleteUser(userId));
        
        User deletedUser = userDao.getUserById(userId);
        assertNull(deletedUser);
    }
    
    @Test
    void testDeleteUser_InvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> userDao.deleteUser(null));
        assertThrows(IllegalArgumentException.class, () -> userDao.deleteUser(0L));
        assertThrows(IllegalArgumentException.class, () -> userDao.deleteUser(-1L));
    }
}