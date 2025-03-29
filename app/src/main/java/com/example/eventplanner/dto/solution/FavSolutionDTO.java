package com.example.eventplanner.dto.solution;

public class FavSolutionDTO {
    private Long id;
    private String name;
    private String description;
    private String mainImageUrl;
    private String city;
    private Double price;
    private Double discount;
    private String categoryName;

    public FavSolutionDTO() {}

    public FavSolutionDTO(Long id, String name, String description, String mainImageUrl, String city,
                          Double price, Double discount, String categoryName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.mainImageUrl = mainImageUrl;
        this.city = city;
        this.price = price;
        this.discount = discount;
        this.categoryName = categoryName;
    }

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}

    public String getMainImageUrl() {return mainImageUrl;}
    public void setMainImageUrl(String mainImageUrl) {this.mainImageUrl = mainImageUrl;}

    public String getCity() {return city;}
    public void setCity(String city) {this.city = city;}

    public Double getPrice() {return price;}
    public void setPrice(Double price) {this.price = price;}

    public Double getDiscount() { return discount; }
    public void setDiscount(Double discount) { this.discount = discount; }

    public String getCategoryName() {return categoryName;}
    public void setCategoryName(String categoryName) {this.categoryName = categoryName;}

}

