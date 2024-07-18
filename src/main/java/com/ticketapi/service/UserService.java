package com.ticketapi.service;

import com.ticketapi.model.User;
import com.ticketapi.util.UserQueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.time.LocalDateTime;

@Service
public class UserService {
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPasswordHash());
        user.setPasswordHash(encodedPassword);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcTemplate.update(
                con -> {
                    PreparedStatement ps = con.prepareStatement(
                            UserQueries.CREATE_USER.getQuery(),
                            new String[] {"id"}
                    );
                    ps.setString(1, user.getUsername());
                    ps.setString(2, user.getEmail());
                    ps.setString(3, user.getPasswordHash());
                    ps.setObject(4, LocalDateTime.now());
                    ps.setObject(5, LocalDateTime.now());
                    ps.setBoolean(6, true);
                    return ps;
                },
                keyHolder
        );

        if (rowsAffected > 0) {
            user.setId(keyHolder.getKey().longValue());
            return user;
        } else {
            throw new RuntimeException("Failed to create user");
        }
    }

    public User getUserById(Long id) {
        return jdbcTemplate.queryForObject(
                UserQueries.GET_USER_BY_ID.getQuery(),
                new Object[]{id},
                this::mapRowToUser
        );
    }

    public User getUserByUsername(String username) {
        return jdbcTemplate.queryForObject(
                UserQueries.GET_USER_BY_USERNAME.getQuery(),
                new Object[]{username},
                this::mapRowToUser
        );
    }

    public void updateUser(User user) {
        jdbcTemplate.update(
                UserQueries.UPDATE_USER.getQuery(),
                user.getUsername(),
                user.getEmail(),
                Timestamp.valueOf(LocalDateTime.now()),
                user.getId()
        );
    }

    public void deleteUser(Long id) {
        jdbcTemplate.update(
                UserQueries.DELETE_USER.getQuery(),
                id
        );
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        return new User(
                rs.getLong("id"),
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("updated_at").toLocalDateTime(),
                rs.getBoolean("is_active")
        );
    }
}