package com.ticketapi.service;

import com.ticketapi.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class UserServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_ShouldReturnUserWithId() {
        User user = new User("testuser", "test@example.com", "password");
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(jdbcTemplate.update(any(PreparedStatementCreator.class), any(KeyHolder.class)))
                .thenAnswer(invocation -> {
                    KeyHolder keyHolder = invocation.getArgument(1);
                    keyHolder.getKeyList().add(Collections.singletonMap("id", 1L));
                    return 1;
                });

        User createdUser = userService.createUser(user);

        assertNotNull(createdUser.getId());
        assertEquals(1L, createdUser.getId());
        verify(passwordEncoder).encode("password");
        verify(jdbcTemplate).update(any(PreparedStatementCreator.class), any(KeyHolder.class));
    }

    @Test
    void getUserById_ShouldReturnUser() {
        User expectedUser = new User(1L, "testuser", "test@example.com", "encodedPassword", LocalDateTime.now(), LocalDateTime.now(), true);
        when(jdbcTemplate.queryForObject(anyString(), any(Object[].class), any(RowMapper.class))).thenReturn(expectedUser);

        User user = userService.getUserById(1L);

        assertNotNull(user);
        assertEquals(expectedUser.getId(), user.getId());
        assertEquals(expectedUser.getUsername(), user.getUsername());
    }

    @Test
    void getUserByUsername_ShouldReturnUser() {
        String username = "testuser";
        User expectedUser = new User(1L, username, "test@example.com", "encodedPassword", LocalDateTime.now(), LocalDateTime.now(), true);

        when(jdbcTemplate.queryForObject(
                eq("SELECT * FROM users WHERE username = ?"),
                eq(new Object[]{username}),
                any(RowMapper.class)
        )).thenReturn(expectedUser);

        User user = userService.getUserByUsername(username);

        assertNotNull(user);
        assertEquals(expectedUser.getId(), user.getId());
        assertEquals(expectedUser.getUsername(), user.getUsername());
        assertEquals(expectedUser.getEmail(), user.getEmail());
        verify(jdbcTemplate).queryForObject(
                eq("SELECT * FROM users WHERE username = ?"),
                eq(new Object[]{username}),
                any(RowMapper.class)
        );
    }


}