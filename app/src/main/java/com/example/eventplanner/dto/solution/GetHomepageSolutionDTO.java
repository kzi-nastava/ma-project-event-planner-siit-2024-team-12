package com.example.eventplanner.dto.solution;


import com.example.eventplanner.activities.homepage.CardItem;

public class GetHomepageSolutionDTO implements CardItem {
    private Long id;
    private String name;
    private String imageUrl;
    private String type;
    private Double discount;
    private String description;
    private Double price;
    private Double rating;
    private String city;
    private String categoryName;
    private Long ownerId;



    public GetHomepageSolutionDTO(Long id, String name, String imageUrl, String type, Double discount, String description, Double price, Double rating, String city, String categoryName, Long ownerId) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.type = type;
        this.discount = discount;
        this.description = description;
        this.price = price;
        this.rating = rating;
        this.city = city;
        this.categoryName = categoryName;
        this.ownerId = ownerId;
    }

    public GetHomepageSolutionDTO() {
    }

    public Long getId() { return id;}
    public void setId(Long id) { this.id = id;}
    public String getName() { return name;}
    public void setName(String name) { this.name = name;}
    public String getImageUrl() { return imageUrl;}
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl;}
    public String getType() { return type;}
    public void setType(String type) { this.type = type;}
    public Double getDiscount() { return discount;}
    public void setDiscount(Double discount) { this.discount = discount;}
    public String getDescription() { return description;}
    public void setDescription(String description) { this.description = description;}
    public Double getPrice() { return price;}
    public void setPrice(Double price) { this.price = price;}
    public Double getRating() { return rating;}
    public void setRating(Double rating) { this.rating = rating;}
    public String getCity() { return city;}
    public void setCity(String city) { this.city = city;}
    public String getCategoryName() { return categoryName;}
    public void setCategoryName(String categoryName) { this.categoryName = categoryName;}
    public Long  getOwnerId() { return ownerId;}
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId;}

}
