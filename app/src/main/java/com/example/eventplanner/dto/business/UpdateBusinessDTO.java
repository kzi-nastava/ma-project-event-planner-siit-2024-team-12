package com.example.eventplanner.dto.business;

import java.util.List;

public class UpdateBusinessDTO {
    private String address;
    private String phone;
    private String description;
    private List<String> imageUrls;

    public UpdateBusinessDTO() {super();}

    public UpdateBusinessDTO(String address, String phone, String description, List<String> imageUrls) {
        this.address = address;
        this.phone = phone;
        this.description = description;
        this.imageUrls = imageUrls;

    }

    public String getAddress() {return address;}
    public void setAddress(String address) {this.address = address;}

    public String getPhone() {return phone;}
    public void setPhone(String phone) {this.phone = phone;}

    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}

    public List<String> getImageUrls() {return imageUrls;}
    public void setImageUrls(List<String> imageUrls) {this.imageUrls = imageUrls;}

}
