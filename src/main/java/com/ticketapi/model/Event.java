package com.ticketapi.model;

import java.time.LocalDateTime;

public class Event {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime dateTime;
    private String venue;
    private int totalTickets;
    private int availableTickets;

    public Event(Long id, String name, String description, LocalDateTime dateTime, String venue, int totalTickets, int availableTickets) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.dateTime = dateTime;
        this.venue = venue;
        this.totalTickets = totalTickets;
        this.availableTickets = availableTickets;
    }
    public Event(LocalDateTime dateTime){
        this.dateTime = dateTime;
    }

    public Event() {

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
}
