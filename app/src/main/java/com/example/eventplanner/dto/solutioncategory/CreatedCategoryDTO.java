package com.example.eventplanner.dto.solutioncategory;

import com.example.eventplanner.enumeration.Status;

public class CreatedCategoryDTO {
    private Long id;
    private String name;
    private String description;
    private Status status;

    public CreatedCategoryDTO() {
    }

    public CreatedCategoryDTO(Long id, String name, String description, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
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
