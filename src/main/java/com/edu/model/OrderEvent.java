package com.edu.model;

import java.util.List;

public class OrderEvent {
    private Long orderId;
    private String status;
    private List<Long> productIds; // <--- Add this field

    public OrderEvent() {} // Required for JSON Deserialization

    public OrderEvent(Long orderId, String status,  List<Long> productIds) {
        this.orderId = orderId;
        this.status = status;
        this.productIds = productIds;

    }

    // Getters and Setters
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<Long> getProductIds() { return productIds; }
    public void setProductIds(List<Long> productIds) { this.productIds = productIds; }
}