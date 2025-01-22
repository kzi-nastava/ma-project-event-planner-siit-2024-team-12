package com.example.eventplanner.dto.solutioncategory;

public class GetSolutionCategoryDTO {
    private String name;

    public GetSolutionCategoryDTO() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "DTO [name: " + name + " ]";
    }

}
