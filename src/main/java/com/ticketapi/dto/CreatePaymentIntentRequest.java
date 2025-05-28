package com.ticketapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreatePaymentIntentRequest {
    
    @JsonProperty("eventId")
    private Long eventId;
    
    @JsonProperty("ticketType")
    private String ticketType;
    
    @JsonProperty("price")
    private Double price;
    
    @JsonProperty("quantity")
    private Integer quantity = 1; // Default to 1 ticket
    
    // Constructors
    public CreatePaymentIntentRequest() {}
    
    public CreatePaymentIntentRequest(Long eventId, String ticketType, Double price) {
        this.eventId = eventId;
        this.ticketType = ticketType;
        this.price = price;
    }
    
    // Getters and Setters
    public Long getEventId() {
        return eventId;
    }
    
    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
    
    public String getTicketType() {
        return ticketType;
    }
    
    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }
    
    public Double getPrice() {
        return price;
    }
    
    public void setPrice(Double price) {
        this.price = price;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    @Override
    public String toString() {
        return "CreatePaymentIntentRequest{" +
                "eventId=" + eventId +
                ", ticketType='" + ticketType + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}