package com.example.eventplanner.dto.product;


import com.example.eventplanner.model.CategoryRecommendation;

import java.util.List;

public class CreateProductDTO {
    private String name;
    private String description;
    private List<String> eventTypeNames;
    private String category;
    private Double price;
    private Double discount;
    private String imageUrl;
    private Boolean isAvailable;
    private Boolean isVisible;
    private CategoryRecommendation categoryRecommendation;


    public CreateProductDTO() {super();}


    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}

    public List<String> getEventTypeNames() {return eventTypeNames;}
    public void setEventTypeNames(List<String> eventTypeNames) {this.eventTypeNames = eventTypeNames;}

    public String getCategory() {return category;}
    public void setCategory(String category) {this.category = category;}

    public Double getPrice() {return price;}
    public void setPrice(Double price) {this.price = price;}

    public Double getDiscount() {return discount;}
    public void setDiscount(Double discount) {this.discount = discount;}

    public Boolean getIsAvailable() {return isAvailable;}
    public void setIsAvailable(Boolean isAvailable) {this.isAvailable = isAvailable;}


    public String getImageUrl() {return imageUrl;}
    public void setImageUrl(String imageUrl) {this.imageUrl = imageUrl;}

    public Boolean getIsVisible() {return this.isVisible;}
    public void setIsVisible(Boolean isVisible) {this.isVisible = isVisible;}

    public CategoryRecommendation getCategoryRecommendation() {return categoryRecommendation;}
    public void setCategoryRecommendation(CategoryRecommendation categoryRecommendation) {this.categoryRecommendation = categoryRecommendation;}

    @Override
    public String toString() {
        return "CreateProductDTO {name=" + this.name + ", description=" + this.description + ", eventTypes=" + this.eventTypeNames + ", category="
                + this.category + " isAvailable=" + this.isAvailable + ", isVisible=" + this.isVisible + ", categoryRecommendation=" + this.categoryRecommendation + "}";
    }
}

