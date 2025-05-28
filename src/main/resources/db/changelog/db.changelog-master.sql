--liquibase formatted sql

--changeset Girmay:1
CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       username VARCHAR(255) NOT NULL UNIQUE,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       is_active BOOLEAN NOT NULL DEFAULT TRUE
);
--rollback DROP TABLE users;

--changeset Girmay:2
CREATE TABLE events (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        description TEXT,
                        date_time TIMESTAMP NOT NULL,
                        venue VARCHAR(255) NOT NULL,
                        total_tickets INT NOT NULL,
                        available_tickets INT NOT NULL
);
--rollback DROP TABLE events;

--changeset Girmay:3
CREATE TABLE tickets (
                         id SERIAL PRIMARY KEY,
                         event_id INT NOT NULL,
                         user_id INT NOT NULL,
                         ticket_type VARCHAR(255),
                         price DECIMAL(10, 2) NOT NULL,
                         FOREIGN KEY (event_id) REFERENCES events(id),
                         FOREIGN KEY (user_id) REFERENCES users(id)
);
--rollback DROP TABLE tickets;

--changeset Girmay:4
CREATE TABLE orders (
                        id SERIAL PRIMARY KEY,
                        user_id INT NOT NULL,
                        event_id INT NOT NULL,
                        ticket_type VARCHAR(255) NOT NULL,
                        price DECIMAL(10, 2) NOT NULL,
                        quantity INT NOT NULL DEFAULT 1,
                        status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
                        payment_intent_id VARCHAR(255),
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (user_id) REFERENCES users(id),
                        FOREIGN KEY (event_id) REFERENCES events(id)
);
--rollback DROP TABLE orders;