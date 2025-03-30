package com.example.eventplanner.dto.eventtype;

import java.util.List;



public class GetEventTypeDTO {
    private Long id;
    private String name;
    private String description;
    private List<String> suggestedCategoryNames;
    private Boolean isActive;
    private boolean isExpanded;




    public GetEventTypeDTO() { super(); }

    public GetEventTypeDTO(Long id, String name, String description, Boolean isActive, List<String> suggestedCategoryNames) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isActive = isActive;
        this.suggestedCategoryNames = suggestedCategoryNames;
        this.isExpanded = false;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getSuggestedCategoryNames() { return suggestedCategoryNames; }
    public void setSuggestedCategoryNames(List<String> suggestedCategoryNames) {this.suggestedCategoryNames = suggestedCategoryNames; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public boolean getIsExpanded() { return isExpanded; }
    public void setIsExpanded(boolean isExpanded) { this.isExpanded = isExpanded; }

    @Override
    public String toString() {
        return "GetEventTypeDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", suggestedCategoryNames=" + suggestedCategoryNames +
                ", isActive=" + isActive +
                '}';
    }
}
