package com.example.eventplanner.dto.budget;

import com.example.eventplanner.dto.solutioncategory.GetCategoryDTO;
import com.example.eventplanner.dto.solutioncategory.GetSolutionCategoryDTO;

import java.io.Serializable;
import java.util.List;

public class GetBudgetItemDTO implements Serializable {
    private Long id;
    private String name;
    private Double cost;
    private GetCategoryDTO category;
    private List<GetPurchaseAndReservationForBudgetDTO> solutions;

    public GetBudgetItemDTO() {
    }

    public GetBudgetItemDTO(Long id, String name, Double cost, GetCategoryDTO category, List<GetPurchaseAndReservationForBudgetDTO> solutions) {
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.category = category;
        this.solutions = solutions;
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

    public GetCategoryDTO getCategory() {
        return category;
    }

    public void setCategory(GetCategoryDTO category) {
        this.category = category;
    }

    public List<GetPurchaseAndReservationForBudgetDTO> getSolutions() {
        return solutions;
    }

    public void setSolutions(List<GetPurchaseAndReservationForBudgetDTO> solutions) {
        this.solutions = solutions;
    }
}
