package com.ticketapi.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Event {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime dateTime;
    private String venue;
    private int totalTickets;
    private int availableTickets;
    private BigDecimal price;

    public Event(Long id, String name, String description, LocalDateTime dateTime, String venue, int totalTickets, int availableTickets, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.dateTime = dateTime;
        this.venue = venue;
        this.totalTickets = totalTickets;
        this.availableTickets = availableTickets;
        this.price = price;
    }
    
    public Event(LocalDateTime dateTime){
        this.dateTime = dateTime;
    }

    public Event() {
        this.price = BigDecimal.ZERO; // Default to 0.00 if not specified
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public int getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(int totalTickets) {
        this.totalTickets = totalTickets;
    }

    public int getAvailableTickets() {
        return availableTickets;
    }

    public void setAvailableTickets(int availableTickets) {
        this.availableTickets = availableTickets;
    }
    
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
    
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
