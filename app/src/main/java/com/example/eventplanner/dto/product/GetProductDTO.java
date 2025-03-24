package com.example.eventplanner.dto.product;


import java.util.List;

public class GetProductDTO {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Double discount;
    private Boolean isAvailable;
    private Boolean isVisible;
    private String categoryName;
    private List<String> eventTypeNames;
    private String mainImageUrl;
    private String city;

    public GetProductDTO() {super();}

    public GetProductDTO(Long id, String name, String description, Double price, Double discount,
                         Boolean isAvailable, Boolean isVisible, String categoryName,
                         List<String> eventTypeNames, String mainImageUrl, String city) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.discount = discount;
        this.isAvailable = isAvailable;
        this.isVisible = isVisible;
        this.categoryName = categoryName;
        this.eventTypeNames = eventTypeNames;
        this.mainImageUrl = mainImageUrl;
        this.city = city;
    }

    public Long getId() {return this.id;}
    public void setId(Long id) {this.id = id;}

    public String getName() {return this.name;}
    public void setName(String name) {this.name = name;}

    public String getDescription() {return this.description;}
    public void setDescription(String description) {this.description = description;}

    public Double getPrice() {return this.price;}
    public void setPrice(Double price) {this.price = price;}

    public Double getDiscount() {return this.discount;}
    public void setDiscount(Double discount) {this.discount = discount;}

    public Boolean getIsAvailable() {return this.isAvailable;}
    public void setIsAvailable(Boolean isAvailable) {this.isAvailable = isAvailable;}

    public String getCategoryName() {return this.categoryName;}
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<String> getEventTypeNames() {return this.eventTypeNames;}
    public void setEventTypeNames(List<String> eventTypeNames) {this.eventTypeNames = eventTypeNames;}

    public Boolean getIsVisible() { return this.isVisible; }
    public void setIsVisible(Boolean isVisible) { this.isVisible = isVisible; }

    public String getMainImageUrl() { return this.mainImageUrl; }
    public void setMainImageUrl(String mainImageUrl) { this.mainImageUrl = mainImageUrl; }

    public String getCity() { return this.city; }
    public void setCity(String city) { this.city = city; }

    @Override
    public String toString() {
        return "id=" + id + ", name=" + name + ", description=" + description + ", price=" + price
                + ", discount=" + discount + ", isAvailable=" + isAvailable + ", isVisible=" + isVisible
                + ", categoryName=" + categoryName + ", eventTypeNames=" + eventTypeNames + ", mainImageUrl="
                + mainImageUrl + ", city=" + city;
    }

}

