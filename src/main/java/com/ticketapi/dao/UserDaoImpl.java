package com.ticketapi.dao;

import com.ticketapi.model.User;
import com.ticketapi.util.DatabaseQueries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Repository
public class UserDaoImpl implements UserDao {
    private static final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);
    private final JdbcTemplate jdbcTemplate;

    public UserDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User createUser(User user) {
        logger.debug("Executing SQL: {} with params: {}, {}, {}, {}, {}, {}",
                DatabaseQueries.CREATE_USER.getQuery(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                true);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(
                    connection -> {
                        PreparedStatement ps = connection.prepareStatement(
                                DatabaseQueries.CREATE_USER.getQuery(),
                                new String[]{"id"}
                        );
                        ps.setString(1, user.getUsername());
                        ps.setString(2, user.getEmail());
                        ps.setString(3, user.getPassword());  // This is actually returning passwordHash
                        ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
                        ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                        ps.setBoolean(6, true);
                        return ps;
                    },
                    keyHolder
            );

            if (keyHolder.getKey() != null) {
                user.setId(keyHolder.getKey().longValue());
                return user;
            }
        } catch (DataAccessException e) {
            logger.error("Failed to create user: {}", user.getUsername(), e);
            throw e;  // Re-throw the exception to propagate it
        }
        return user;
    }

    @Override
    public User getUserById(Long id) {
        try {
            return jdbcTemplate.queryForObject(
                    DatabaseQueries.GET_USER_BY_ID.getQuery(),
                    new Object[]{id},
                    this::mapRowToUser
            );
        } catch (EmptyResultDataAccessException e) {
            logger.error("User not found with ID: {}", id, e);
        } catch (DataAccessException e) {
            logger.error("Error fetching user with ID: {}", id, e);
        }
        return new User();
    }

    @Override
    public User getUserByUsername(String username) {
        try {
            return jdbcTemplate.queryForObject(
                    DatabaseQueries.GET_USER_BY_USERNAME.getQuery(),
                    new Object[]{username},
                    this::mapRowToUser
            );
        } catch (EmptyResultDataAccessException e) {
            logger.error("User not found with username: {}", username, e);
        } catch (DataAccessException e) {
            logger.error("Error fetching user with username: {}", username, e);
        }
        return new User();
    }

    @Override
    public void updateUser(User user) {
        try {
            int rowsAffected = jdbcTemplate.update(
                    DatabaseQueries.UPDATE_USER.getQuery(),
                    user.getUsername(),
                    user.getEmail(),
                    Timestamp.valueOf(LocalDateTime.now()),
                    user.getId()
            );
            if (rowsAffected == 0) {
                logger.warn("No user updated with ID: {}", user.getId());
            }
        } catch (DataAccessException e) {
            logger.error("Error updating user with ID: {}", user.getId(), e);
        }
    }

    @Override
    public void deleteUser(Long id) {
        try {
            int rowsAffected = jdbcTemplate.update(DatabaseQueries.DELETE_USER.getQuery(), id);
            if (rowsAffected == 0) {
                logger.warn("No user deleted with ID: {}", id);
            }
        } catch (DataAccessException e) {
            logger.error("Error deleting user with ID: {}", id, e);
        }
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