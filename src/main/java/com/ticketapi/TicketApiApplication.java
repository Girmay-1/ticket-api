package com.ticketapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class
 *
 */
@SpringBootApplication
public class TicketApiApplication {

    public static void main( String[] args )
    {

        System.out.println("DB_URL: " + System.getenv("DB_URL"));
        System.out.println("DB_USERNAME: " + System.getenv("DB_USERNAME"));
        System.out.println("DB_PASSWORD: " + System.getenv("DB_PASSWORD"));
        System.out.println("STRIPE_SECRET_KEY: " + System.getenv("STRIPE_SECRET_KEY"));

        SpringApplication.run(TicketApiApplication.class, args);
    }
}
