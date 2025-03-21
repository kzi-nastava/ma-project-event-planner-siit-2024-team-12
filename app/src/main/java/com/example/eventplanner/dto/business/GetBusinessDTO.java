package com.example.eventplanner.dto.business;

public class GetBusinessDTO {
    private Long id;
    private String companyName;
    private String companyEmail;
    private String address;
    private String phone;
    private String description;
    private String owner;

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

    @Override
    public String toString() {
        return "email= " + companyEmail;
    }

}
