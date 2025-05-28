package com.ticketapi.service;

import com.ticketapi.dao.UserDao;
import com.ticketapi.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userDao.createUser(user);
    }

    public boolean validateUser(String username, String password) {
        User user = userDao.getUserByUsername(username);
        return user != null && passwordEncoder.matches(password, user.getPassword());
    }

    public Long getUserIdByUsername(String username) {
        User user = userDao.getUserByUsername(username);
        if (user == null) {
            return null;
        }
        return user.getId();
    }
}