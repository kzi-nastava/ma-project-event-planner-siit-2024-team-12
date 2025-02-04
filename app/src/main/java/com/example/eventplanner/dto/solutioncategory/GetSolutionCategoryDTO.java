package com.example.eventplanner.dto.solutioncategory;

public class GetSolutionCategoryDTO {
    private String id;
    private String name;
    private String description;
    private String status;
    private boolean isExpanded;

    public GetSolutionCategoryDTO(String id, String name, String description, String status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.isExpanded = false;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isExpanded() { return isExpanded; }
    public void setExpanded(boolean expanded) { this.isExpanded = expanded; }

}
