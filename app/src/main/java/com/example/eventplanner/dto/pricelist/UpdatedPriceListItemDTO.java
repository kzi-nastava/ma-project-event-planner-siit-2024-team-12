package com.example.eventplanner.dto.pricelist;

import java.io.Serializable;

public class UpdatedPriceListItemDTO implements Serializable {
    private Long id;
    private Double discountPrice;
    private UpdatePriceListSolutionDTO solution;

    public UpdatedPriceListItemDTO() {
    }

    public UpdatedPriceListItemDTO(Long id, Double discountPrice, UpdatePriceListSolutionDTO solution) {
        this.id = id;
        this.discountPrice = discountPrice;
        this.solution = solution;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(Double discountPrice) {
        this.discountPrice = discountPrice;
    }

    public UpdatePriceListSolutionDTO getSolution() {
        return solution;
    }

    public void setSolution(UpdatePriceListSolutionDTO solution) {
        this.solution = solution;
    }
}
