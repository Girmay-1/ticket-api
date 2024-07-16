package com.ticketapi.util;
public enum UserQueries {
    CREATE_USER("""
        INSERT INTO users (username, email, password_hash, created_at, updated_at, is_active)
        VALUES (?, ?, ?, ?, ?, ?)
    """),
    GET_USER_BY_ID("""
        SELECT * FROM users WHERE id = ?
    """),
    GET_USER_BY_USERNAME("""
        SELECT * FROM users WHERE username = ?
    """),
    UPDATE_USER("""
        UPDATE users SET username = ?, email = ?, updated_at = ? WHERE id = ?
    """),
    DELETE_USER("""
        DELETE FROM users WHERE id = ?
    """);

    private final String query;

    UserQueries(String query) {
        this.query = query;
    }

    public String getQuery() {
        return this.query;
    }
}