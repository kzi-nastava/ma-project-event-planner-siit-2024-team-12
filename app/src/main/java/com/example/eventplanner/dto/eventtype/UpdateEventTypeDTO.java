package com.example.eventplanner.dto.eventtype;

import java.util.List;

public class UpdateEventTypeDTO {
    private String description;
    private List<String> suggestedCategoryNames;

    public UpdateEventTypeDTO() {}


    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getSuggestedCategoryNames() { return suggestedCategoryNames; }
    public void setSuggestedCategoryNames(List<String> categories) { this.suggestedCategoryNames = categories; }

    @Override
    public String toString() {
        return "UpdateEventTypeDTO{" +
                "description='" + description + '\'' +
                ", suggestedCategoryNames=" + suggestedCategoryNames +
                '}';
    }

}
