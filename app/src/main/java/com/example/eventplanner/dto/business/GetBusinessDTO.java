package com.example.eventplanner.dto.business;

import java.util.List;

public class GetBusinessDTO {
    private Long id;
    private String companyName;
    private String companyEmail;
    private String address;
    private String phone;
    private String description;
    private String owner;
    private String mainImageUrl;
    private List<String> imageUrls;

    public GetBusinessDTO() {super();}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public String getOwner() {return owner;}
    public void setOwner(String owner) {this.owner = owner;}

    public String getMainImageUrl() {return mainImageUrl;}
    public void setMainImageUrl(String mainImageUrl) {this.mainImageUrl = mainImageUrl;}

    public List<String> getImageUrls() {return imageUrls;}
    public void setImageUrls(List<String> imageUrls) {this.imageUrls = imageUrls;}

    @Override
    public String toString() {
        return "BusinessDTO [id=" + id + ", companyName=" + companyName + ", companyEmail=" + companyEmail
                + ", address=" + address + ", phone=" + phone + ", description=" + description + ", owner=" + owner;
    }
}
