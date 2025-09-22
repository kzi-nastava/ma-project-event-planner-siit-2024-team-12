package com.example.eventplanner.dto.solutioncategory;

import com.example.eventplanner.enumeration.Status;

public class CreateCategoryDTO {
    private String name;
    private String description;
    private Status status;

    public CreateCategoryDTO() {
    }

    public CreateCategoryDTO(String name, String description, Status status) {
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
