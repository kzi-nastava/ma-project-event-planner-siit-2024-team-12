package com.example.eventplanner.dto.product;


import java.util.List;

// cannot change product's category
public class UpdatedProductDTO {
    private Long id;
    private String name;
    private String description;
    private String categoryName;
    private Double price;
    private Double discount;
    private String mainImageUrl;
    private List<String> eventTypeNames;
    private Boolean isAvailable;
    private Boolean isVisible;


    public UpdatedProductDTO() {super();}

    public Long getId() {return this.id;}
    public void setId(Long id) {this.id = id;}

    public String getName() {return this.name;}
    public void setName(String name) {this.name = name;}

    public String getDescription() {return this.description;}
    public void setDescription(String description) {this.description = description;}

    public String getCategoryName() {return this.categoryName;}
    public void setCategoryName(String categoryName) {this.categoryName = categoryName;}

    public Double getPrice() {return this.price;}
    public void setPrice(Double price) {this.price = price;}

    public Double getDiscount() {return this.discount;}
    public void setDiscount(Double discount) {this.discount = discount;}

    public Boolean getIsAvailable() {return this.isAvailable;}
    public void setIsAvailable(Boolean isAvailable) {this.isAvailable = isAvailable;}


    public List<String> getEventTypeNames() {return this.eventTypeNames;}
    public void setEventTypeNames(List<String> eventTypeNames) {this.eventTypeNames = eventTypeNames;}


    public String getMainImageUrl() {return this.mainImageUrl;}
    public void setMainImageUrl(String mainImageUrl) {this.mainImageUrl = mainImageUrl;}

    public Boolean getIsVisible() {return this.isVisible;}
    public void setIsVisible(Boolean isVisible) {this.isVisible = isVisible;}


    @Override
    public String toString() {
        return "UpdatedDTO {name=" + name + ", isAvailable=" +
                isAvailable + ", isVisible=" + isVisible + "}";
    }
}

