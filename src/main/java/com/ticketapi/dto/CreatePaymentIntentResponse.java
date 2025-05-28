package com.ticketapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreatePaymentIntentResponse {
    
    @JsonProperty("clientSecret")
    private String clientSecret;
    
    @JsonProperty("paymentIntentId")
    private String paymentIntentId;
    
    @JsonProperty("amount")
    private Long amount; // Amount in cents
    
    @JsonProperty("currency")
    private String currency;
    
    @JsonProperty("orderId")
    private Long orderId;
    
    @JsonProperty("status")
    private String status;
    
    // Constructors
    public CreatePaymentIntentResponse() {}
    
    public CreatePaymentIntentResponse(String clientSecret, String paymentIntentId, 
                                     Long amount, String currency, Long orderId) {
        this.clientSecret = clientSecret;
        this.paymentIntentId = paymentIntentId;
        this.amount = amount;
        this.currency = currency;
        this.orderId = orderId;
        this.status = "requires_payment_method";
    }
    
    // Getters and Setters
    public String getClientSecret() {
        return clientSecret;
    }
    
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
    
    public String getPaymentIntentId() {
        return paymentIntentId;
    }
    
    public void setPaymentIntentId(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
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
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "CreatePaymentIntentResponse{" +
                "paymentIntentId='" + paymentIntentId + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", orderId=" + orderId +
                ", status='" + status + '\'' +
                '}';
    }
}