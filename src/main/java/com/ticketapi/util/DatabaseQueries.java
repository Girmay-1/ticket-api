package com.ticketapi.util;
public enum DatabaseQueries {
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
    """),
    CREATE_EVENT("""
            INSERT INTO events (name, description, date_time, venue, total_tickets, available_tickets) VALUES (?, ?, ?, ?, ?, ?)"""
    ),
    GET_EVENT_BY_ID("""
            SELECT * FROM events WHERE id = ?"""
    ),
    GET_ALL_EVENTS("""
            SELECT * FROM events"""
    ),
    UPDATE_EVENT("""
            UPDATE events SET name = ?, description = ?, date_time = ?, venue = ?, total_tickets = ?, available_tickets = ? WHERE id = ?
            """),
    DELETE_EVENT("""
           DELETE FROM events WHERE id = ?
           """),
    CREATE_TICKET("""
            INSERT INTO tickets (event_id, user_id, ticket_type, price) VALUES (?, ?, ?, ?)
            """),
    GET_TICKET_BY_ID("""
            SELECT * FROM tickets WHERE id = ?
            """),
    GET_ALL_TICKETS("""
            SELECT * FROM tickets
            """),
    UPDATE_TICKET("""
            UPDATE tickets SET event_id = ?, user_id = ?, ticket_type = ?, price = ? WHERE id = ?
            """),
    DELETE_TICKET("""
            DELETE FROM tickets WHERE id = ?
            """), GET_TICKET_BY_USERNAME("""
            SELECT t.* FROM tickets t 
            JOIN users u ON t.user_id = u.id 
            WHERE u.username = ?"""),
    
    // Order related queries
    CREATE_ORDER("""
        INSERT INTO orders (user_id, event_id, ticket_type, price, quantity, status, created_at, updated_at) 
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    """),
    GET_ORDER_BY_ID("""
        SELECT * FROM orders WHERE id = ?
    """),
    GET_ORDERS_BY_USER_ID("""
        SELECT * FROM orders WHERE user_id = ? ORDER BY created_at DESC
    """),
    UPDATE_ORDER_PAYMENT_INTENT("""
        UPDATE orders SET payment_intent_id = ?, updated_at = ? WHERE id = ?
    """),
    UPDATE_ORDER_STATUS("""
        UPDATE orders SET status = ?, updated_at = ? WHERE id = ?
    """),
    GET_ORDER_BY_PAYMENT_INTENT("""
        SELECT * FROM orders WHERE payment_intent_id = ?
    """),
    DELETE_ORDER("""
        DELETE FROM orders WHERE id = ?
    """);

    private final String query;

    DatabaseQueries(String query) {
        this.query = query;
    }

    public String getQuery() {
        return this.query;
    }
}