package com.example.eventplanner.dto.budget;

import com.example.eventplanner.dto.eventtype.GetEventTypeDTO;

import java.util.List;

public class UpdateBudgetForEventDTO {
    GetEventTypeDTO eventType;
    List<GetBudgetItemDTO> budgetItems;
    Boolean canUpdate;

    public UpdateBudgetForEventDTO() {
    }

    public UpdateBudgetForEventDTO(GetEventTypeDTO eventType, List<GetBudgetItemDTO> budgetItems, Boolean canUpdate) {
        this.eventType = eventType;
        this.budgetItems = budgetItems;
        this.canUpdate = canUpdate;
    }

    public GetEventTypeDTO getEventType() {
        return eventType;
    }

    public void setEventType(GetEventTypeDTO eventType) {
        this.eventType = eventType;
    }

    public List<GetBudgetItemDTO> getBudgetItems() {
        return budgetItems;
    }

    public void setBudgetItems(List<GetBudgetItemDTO> budgetItems) {
        this.budgetItems = budgetItems;
    }

    public Boolean getCanUpdate() {
        return canUpdate;
    }

    public void setCanUpdate(Boolean canUpdate) {
        this.canUpdate = canUpdate;
    }
}
