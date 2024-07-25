package com.ticketapi.model;

public class Ticket {
    private Long id;
    private Long eventId;
    private Long userId;
    private String ticketType;
    private double price;

    public Ticket(Long id, Long eventId, Long userId, String ticketType, double price) {
        this.id = id;
        this.eventId = eventId;
        this.userId = userId;
        this.ticketType = ticketType;
        this.price = price;
    }

    public Ticket(){}
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
