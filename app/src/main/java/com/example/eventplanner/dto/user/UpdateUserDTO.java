package com.example.eventplanner.dto.user;


public class UpdateUserDTO {
    private String name;
    private String surname;
    private String address;
    private String phone;
    private String role;
    private Boolean isDeactivated;

    public UpdateUserDTO() {super();}


    public UpdateUserDTO(String name, String surname, String address, String phone, Boolean isDeactivated) {
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.phone = phone;
        this.isDeactivated = isDeactivated;
    }



    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getSurname() {return surname;}
    public void setSurname(String surname) {this.surname = surname;}

    public String getAddress() {return address;}
    public void setAddress(String address) {this.address = address;}

    public String getPhone() {return phone;}
    public void setPhone(String phone) {this.phone = phone;}

    public Boolean isDeactivated() {return isDeactivated;}
    public void setDeactivated(Boolean isDeactivated) {this.isDeactivated = isDeactivated;}

}

