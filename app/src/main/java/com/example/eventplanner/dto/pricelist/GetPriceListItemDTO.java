package com.example.eventplanner.dto.pricelist;

import java.io.Serializable;

public class GetPriceListItemDTO implements Serializable {

    private Long id;
    private Double discountPrice;
    private GetPriceListSolutionDTO solution;

    public GetPriceListItemDTO() {
    }

    public GetPriceListItemDTO(Long id, Double discountPrice, GetPriceListSolutionDTO solution) {
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

    public GetPriceListSolutionDTO getSolution() {
        return solution;
    }

    public void setSolution(GetPriceListSolutionDTO solution) {
        this.solution = solution;
    }
}
