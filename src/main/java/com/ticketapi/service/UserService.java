package com.ticketapi.service;

import com.ticketapi.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
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
        // Set the encoded password back to the user object
        user.setPasswordHash(encodedPassword);

        // Insert the user into the database and retrieve the generated ID
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcTemplate.update(
                (PreparedStatementCreator) con -> {
                    PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO users (username, email, password, created_at, updated_at, enabled) VALUES (?, ?, ?, ?, ?, ?)",
                            new String[] {"id"});
                    ps.setString(1, user.getUsername());
                    ps.setString(2, user.getEmail());
                    ps.setString(3, user.getPasswordHash());
                    ps.setObject(4, LocalDateTime.now());
                    ps.setObject(5, LocalDateTime.now());
                    ps.setBoolean(6, true);
                    return ps;
                },
                keyHolder);

        if (rowsAffected > 0) {
            user.setId(keyHolder.getKey().longValue());
            return user;
        } else {
            throw new RuntimeException("Failed to create user");
        }
    }

    public User getUserById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, this::mapRowToUser);
    }

    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{username}, this::mapRowToUser);
    }

    public void updateUser(User user) {
        String sql = """
            UPDATE users SET username = ?, email = ?, updated_at = ? WHERE id = ?
        """;
        jdbcTemplate.update(sql, user.getUsername(), user.getEmail(), Timestamp.valueOf(LocalDateTime.now()), user.getId());
    }

    public void deleteUser(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    private User mapRowToUser(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
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