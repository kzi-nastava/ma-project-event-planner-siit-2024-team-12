package com.example.eventplanner.dto.user;


public class UpdatedUserDTO {
    private String email;
    private String password;
    private String name;
    private String surname;
    private String address;
    private String phone;
    private String role;
    private Boolean isDeactivated;

    public UpdatedUserDTO() {super();}

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}

    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getSurname() {return surname;}
    public void setSurname(String surname) {this.surname = surname;}

    public String getAddress() {return address;}
    public void setAddress(String address) {this.address = address;}

    public String getPhone() {return phone;}
    public void setPhone(String phone) {this.phone = phone;}

    public String getRole() {return role;}
    public void setRole(String userRole) {this.role = userRole;}

    public Boolean isDeactivated() {return isDeactivated;}
    public void setDeactivated(Boolean isDeactivated) {this.isDeactivated = isDeactivated;}


}

