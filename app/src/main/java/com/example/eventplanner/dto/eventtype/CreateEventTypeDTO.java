package com.example.eventplanner.dto.eventtype;

import java.util.List;

public class CreateEventTypeDTO {
    private String name;
    private String description;
    private List<String> categoryNames;

    public CreateEventTypeDTO() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getCategoryNames() { return categoryNames; }
    public void setCategoryNames(List<String> categories) { this.categoryNames = categories; }
}
