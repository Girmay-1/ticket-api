package com.ticketapi.service;

import com.ticketapi.dao.UserDao;
import com.ticketapi.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPasswordHash());
        user.setPasswordHash(encodedPassword);
        User createdUser = userDao.createUser(user);
        if (createdUser.getId() == null) {
            logger.error("Failed to create user: {}", user.getUsername());
        }
        return createdUser;
    }

    public User getUserById(Long id) {
        User user = userDao.getUserById(id);
        if (user.getId() == null) {
            logger.error("Failed to get user by ID: {}", id);
        }
        return user;
    }

    public User getUserByUsername(String username) {
        User user = userDao.getUserByUsername(username);
        if (user.getId() == null) {
            logger.error("Failed to get user by username: {}", username);
        }
        return user;
    }

    public void updateUser(User user) {
        userDao.updateUser(user);
    }

    public void deleteUser(Long id) {
        userDao.deleteUser(id);
    }
}