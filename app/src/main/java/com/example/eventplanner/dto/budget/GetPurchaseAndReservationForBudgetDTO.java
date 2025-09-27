package com.example.eventplanner.dto.budget;

import java.io.Serializable;

public class GetPurchaseAndReservationForBudgetDTO implements Serializable {
    private Long id;
    private Double amount;
    private GetBudgetSolutionDTO solution;

    public GetPurchaseAndReservationForBudgetDTO() {
    }

    public GetPurchaseAndReservationForBudgetDTO(Long id, Double amount, GetBudgetSolutionDTO solution) {
        this.id = id;
        this.amount = amount;
        this.solution = solution;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public GetBudgetSolutionDTO getSolution() {
        return solution;
    }

    public void setSolution(GetBudgetSolutionDTO solution) {
        this.solution = solution;
    }
}
