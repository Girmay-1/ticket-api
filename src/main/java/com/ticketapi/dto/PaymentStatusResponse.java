package com.ticketapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class PaymentStatusResponse {
    
    @JsonProperty("paymentIntentId")
    private String paymentIntentId;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("amount")
    private Long amount;
    
    @JsonProperty("currency")
    private String currency;
    
    @JsonProperty("orderId")
    private Long orderId;
    
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;
    
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
    
    @JsonProperty("eventName")
    private String eventName;
    
    @JsonProperty("ticketType")
    private String ticketType;
    
    // Constructors
    public PaymentStatusResponse() {}
    
    public PaymentStatusResponse(String paymentIntentId, String status, Long amount, 
                                String currency, Long orderId) {
        this.paymentIntentId = paymentIntentId;
        this.status = status;
        this.amount = amount;
        this.currency = currency;
        this.orderId = orderId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getPaymentIntentId() {
        return paymentIntentId;
    }
    
    public void setPaymentIntentId(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Long getAmount() {
        return amount;
    }
    
    public void setAmount(Long amount) {
        this.amount = amount;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public Long getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getEventName() {
        return eventName;
    }
    
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
    
    public String getTicketType() {
        return ticketType;
    }
    
    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }
    
    @Override
    public String toString() {
        return "PaymentStatusResponse{" +
                "paymentIntentId='" + paymentIntentId + '\'' +
                ", status='" + status + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", orderId=" + orderId +
                ", eventName='" + eventName + '\'' +
                ", ticketType='" + ticketType + '\'' +
                '}';
    }
}