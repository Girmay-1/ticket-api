package com.ticketapi.controller;

import com.ticketapi.model.User;
import com.ticketapi.service.CustomUserDetailsService;
import com.ticketapi.service.UserService;
import com.ticketapi.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    @Test
    void testRegisterUser() {
        User user = new User();
        user.setUsername("testuser");

        when(userService.createUser(any(User.class))).thenReturn(user);

        ResponseEntity<?> response = authController.registerUser(user);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(user, response.getBody());
    }

    @Test
    void testLoginUser() {
        User loginUser = new User();
        loginUser.setUsername("testuser");
        loginUser.setPassword("password");

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");

        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtUtil.generateToken("testuser")).thenReturn("test-token");

        ResponseEntity<?> response = authController.loginUser(loginUser);

        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("test-token", responseBody.get("token"));
        assertEquals("testuser", responseBody.get("username"));
    }
}