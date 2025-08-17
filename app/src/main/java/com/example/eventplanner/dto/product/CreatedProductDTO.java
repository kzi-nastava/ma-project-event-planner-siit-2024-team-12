package com.example.eventplanner.dto.product;


import java.util.List;

public class CreatedProductDTO {
    private Long id;
    private String name;
    private String description;
    private List<String> eventTypeNames;
    private String category;
    private Double price;
    private Double discount;
    private String imageUrl;
    private Boolean isAvailable;
    private Boolean isVisible;
    private Long businessId;


    public CreatedProductDTO() {super();}

    public CreatedProductDTO(String name, String description, List<String> eventTypeNames, String category,
                             Double price, Double discount, String imageUrl, Boolean isAvailable, Boolean isVisible,
                             Long businessId) {
        this.name = name;
        this.description = description;
        this.eventTypeNames = eventTypeNames;
        this.category = category;
        this.price = price;
        this.discount = discount;
        this.imageUrl = imageUrl;
        this.isAvailable = isAvailable;
        this.isVisible = isVisible;
        this.businessId = businessId;
    }


    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

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

    public Boolean getIsVisible() {return isVisible;}
    public void setIsVisible(Boolean isVisible) {this.isVisible = isVisible;}

    public Long getBusinessId() {return businessId;}
    public void setBusinessId(Long businessId) {this.businessId = businessId;}

    @Override
    public String toString() {
        return "Created id=" + id + " " + "isVisible=" + isVisible + " " + "isAvailable=" + isAvailable;
    }
}

