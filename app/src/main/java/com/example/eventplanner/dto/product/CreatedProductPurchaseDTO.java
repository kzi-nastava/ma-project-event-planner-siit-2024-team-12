package com.example.eventplanner.dto.product;

import java.time.LocalDate;

public class CreatedProductPurchaseDTO {
    private Long id;
    private LocalDate dateOfPurchase;
    private Double amount;
    private Long productId;
    private Long eventId;

    public CreatedProductPurchaseDTO() {
    }

    public CreatedProductPurchaseDTO(Long id, LocalDate dateOfPurchase, Double amount, Long productId, Long eventId) {
        this.id = id;
        this.dateOfPurchase = dateOfPurchase;
        this.amount = amount;
        this.productId = productId;
        this.eventId = eventId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDateOfPurchase() {
        return dateOfPurchase;
    }

    public void setDateOfPurchase(LocalDate dateOfPurchase) {
        this.dateOfPurchase = dateOfPurchase;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
}
