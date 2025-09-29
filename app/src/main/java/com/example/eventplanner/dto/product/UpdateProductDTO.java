package com.example.eventplanner.dto.product;


import java.util.List;

// cannot change product's category
public class UpdateProductDTO {
    private String name;
    private String description;
    private Double price;
    private Double discount;
    private String mainImageUrl;
    private List<String> eventTypeNames;
    private Boolean isAvailable;
    private Boolean isVisible;



    public UpdateProductDTO() {super();}


    public String getName() {return this.name;}
    public void setName(String name) {this.name = name;}

    public String getDescription() {return this.description;}
    public void setDescription(String description) {this.description = description;}

    public Double getPrice() {return this.price;}
    public void setPrice(Double price) {this.price = price;}

    public Double getDiscount() {return this.discount;}
    public void setDiscount(Double discount) {this.discount = discount;}

    public String getMainImageUrl() {return this.mainImageUrl;}
    public void setMainImageUrl(String mainImageUrl) {this.mainImageUrl = mainImageUrl;}

    public List<String> getEventTypeNames() {return this.eventTypeNames;}
    public void setEventTypeNames(List<String> eventTypeNames) {this.eventTypeNames = eventTypeNames;}

    public Boolean getIsAvailable() {return this.isAvailable;}
    public void setIsAvailable(Boolean isAvailable) {this.isAvailable = isAvailable;}

    public Boolean getIsVisible() {return this.isVisible;}
    public void setIsVisible(Boolean isVisible) {this.isVisible = isVisible;}



    @Override
    public String toString() {
        return "UpdateDTO {name=" + name + ", description=" + description + ", price=" + price + ", discount="
                + discount + ", mainImageUrl=" + mainImageUrl + ", eventTypes=" + eventTypeNames + ", isAvailable=" +
                isAvailable + ", isVisible=" + isVisible + "}";
    }
}

