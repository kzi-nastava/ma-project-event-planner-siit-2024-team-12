package com.example.eventplanner.dto.pricelist;

import java.io.Serializable;

public class UpdatePriceListSolutionDTO implements Serializable {
    private Double price;
    private Double discount;

    public UpdatePriceListSolutionDTO() {
    }

    public UpdatePriceListSolutionDTO(Double price, Double discount) {
        this.price = price;
        this.discount = discount;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }
}
