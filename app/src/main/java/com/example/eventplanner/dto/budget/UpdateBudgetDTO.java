package com.example.eventplanner.dto.budget;

import java.io.Serializable;

public class UpdateBudgetDTO implements Serializable {
    private Long id;
    private String name;
    private Double cost;
    private Long categoryId;

    public UpdateBudgetDTO() {
    }

    public UpdateBudgetDTO(Long id, String name, Double cost, Long categoryId) {
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.categoryId = categoryId;
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

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}
