package com.example.eventplanner.dto.budget;

public class CreateBudgetItemDTO {
    private String name;
    private Double cost;
    private Long categoryId;

    public CreateBudgetItemDTO() {
    }

    public CreateBudgetItemDTO(String name, Double cost, Long categoryId) {
        this.name = name;
        this.cost = cost;
        this.categoryId = categoryId;
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
