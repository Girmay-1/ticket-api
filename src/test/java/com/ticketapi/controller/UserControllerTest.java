package com.ticketapi.controller;

import com.ticketapi.model.User;
import com.ticketapi.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private CustomUserDetailsService userDetailsService;

    @InjectMocks
    private UserController userController;

    @Test
    void testGetUserProfile() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");

        User user = new User();
        user.setUsername("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(user);

        ResponseEntity<?> response = userController.getUserProfile(authentication);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(user, response.getBody());
    }
}