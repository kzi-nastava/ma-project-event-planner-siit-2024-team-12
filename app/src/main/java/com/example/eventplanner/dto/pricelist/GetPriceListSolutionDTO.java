package com.example.eventplanner.dto.pricelist;

import java.io.Serializable;

public class GetPriceListSolutionDTO implements Serializable {
    private Long id;
    private String name;
    private Double price;
    private Double discount;
    private String description;

    public GetPriceListSolutionDTO() {
    }

    public GetPriceListSolutionDTO(Long id, String name, Double price, Double discount, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.discount = discount;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
