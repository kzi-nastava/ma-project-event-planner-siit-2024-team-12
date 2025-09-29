package com.example.eventplanner.dto.solutioncategory;

import com.example.eventplanner.enumeration.Status;

public class UpdateCategoryDTO {
    private String name;
    private String description;
    private Status status;

    public UpdateCategoryDTO() {
    }

    public UpdateCategoryDTO(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
