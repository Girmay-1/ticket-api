package com.ticketapi.service;

import com.ticketapi.dao.UserDao;
import com.ticketapi.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDao userDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void testCreateUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userDao.createUser(any(User.class))).thenReturn(user);

        User createdUser = userService.createUser(user);

        assertNotNull(createdUser);
        assertEquals("testuser", createdUser.getUsername());
        verify(passwordEncoder).encode("password");
        verify(userDao).createUser(user);
    }

    @Test
    void testValidateUser_ValidCredentials() {
        String username = "testuser";
        String password = "password";
        User user = new User();
        user.setUsername(username);
        user.setPassword("encodedPassword");

        when(userDao.getUserByUsername(username)).thenReturn(user);
        when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(true);

        boolean result = userService.validateUser(username, password);

        assertTrue(result);
    }

    @Test
    void testValidateUser_InvalidCredentials() {
        String username = "testuser";
        String password = "wrongpassword";
        User user = new User();
        user.setUsername(username);
        user.setPassword("encodedPassword");

        when(userDao.getUserByUsername(username)).thenReturn(user);
        when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(false);

        boolean result = userService.validateUser(username, password);

        assertFalse(result);
    }

    @Test
    void testValidateUser_UserNotFound() {
        String username = "nonexistentuser";
        String password = "password";

        when(userDao.getUserByUsername(username)).thenReturn(null);

        boolean result = userService.validateUser(username, password);

        assertFalse(result);
    }
}