package com.ticketapi.dao;

import com.ticketapi.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserDaoImplTest {

    @Autowired
    private UserDao userDao;

    @Test
    void testCreateUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");

        User createdUser = userDao.createUser(user);

        assertNotNull(createdUser.getId());
        assertTrue(createdUser.getId() > 0);
        assertEquals("testuser", createdUser.getUsername());
        assertEquals("test@example.com", createdUser.getEmail());
    }

    // Add more tests for other methods...
}