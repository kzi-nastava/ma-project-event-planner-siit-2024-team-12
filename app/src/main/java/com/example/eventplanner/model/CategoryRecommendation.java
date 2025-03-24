package com.example.eventplanner.model;

public class CategoryRecommendation {
    private String name;
    private String description;

    public CategoryRecommendation() {}

    public CategoryRecommendation(String name, String description) {
        this.name = name;
        this.description = description;
    }


    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}

    @Override
    public String toString() {
        return "CategoryRecommendation [name=" + name + ", description=" + description + "]";
    }
}
