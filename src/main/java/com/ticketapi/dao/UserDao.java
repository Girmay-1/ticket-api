package com.ticketapi.dao;

import com.ticketapi.model.User;

public interface UserDao {
    User createUser(User user);
    User getUserById(Long id);
    User getUserByUsername(String username);
    void updateUser(User user);
    void deleteUser(Long id);
}
