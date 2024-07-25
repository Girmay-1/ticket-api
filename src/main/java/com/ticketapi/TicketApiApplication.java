package com.ticketapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Main application class
 *
 */
@SpringBootApplication
@EnableSwagger2
public class TicketApiApplication {

    public static void main( String[] args ) {
        SpringApplication.run(TicketApiApplication.class, args);
    }
}
