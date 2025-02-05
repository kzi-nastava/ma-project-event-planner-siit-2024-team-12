package com.example.eventplanner.dto.business;

import java.util.List;

public class CreateBusinessDTO {
    private String companyName;
    private String companyEmail;
    private String address;
    private String phone;
    private String description;
    private List<String> imageUrls;
    private String owner;

    public CreateBusinessDTO() {
        super();
    }

    public String getCompanyName() {return companyName;}
    public void setCompanyName(String companyName) {this.companyName = companyName;}

    public String getCompanyEmail() {return companyEmail;}
    public void setCompanyEmail(String companyEmail) {this.companyEmail = companyEmail;}

    public String getAddress() {return address;}
    public void setAddress(String address) {this.address = address;}

    public String getPhone() {return phone;}
    public void setPhone(String phone) {this.phone = phone;}

    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}

    public List<String> getImageUrls() {return imageUrls;}
    public void setImageUrls(List<String> imageUrls) {this.imageUrls = imageUrls;}

    public String getOwner() {return owner;}
    public void setOwner(String owner) {this.owner = owner;}

}
