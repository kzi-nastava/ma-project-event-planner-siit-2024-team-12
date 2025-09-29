package com.example.eventplanner.dto.business;

public class GetBusinessAndProviderDTO {
    private Long businessId;
    private Long providerId;
    private String companyName;
    private String providerName;
    private String providerSurname;
    private String companyAddress;
    private String companyPhone;
    private String providerPhone;
    private String companyMainImage;
    private String providerMainImage;
    private String companyEmail;
    private String providerEmail;
    private String companyDescription;

    public GetBusinessAndProviderDTO() {
    }

    public GetBusinessAndProviderDTO(Long businessId, Long providerId, String companyName, String providerName, String providerSurname, String companyAddress, String companyPhone, String providerPhone, String companyMainImage, String providerMainImage, String companyEmail, String providerEmail, String companyDescription) {
        this.businessId = businessId;
        this.providerId = providerId;
        this.companyName = companyName;
        this.providerName = providerName;
        this.providerSurname = providerSurname;
        this.companyAddress = companyAddress;
        this.companyPhone = companyPhone;
        this.providerPhone = providerPhone;
        this.companyMainImage = companyMainImage;
        this.providerMainImage = providerMainImage;
        this.companyEmail = companyEmail;
        this.providerEmail = providerEmail;
        this.companyDescription = companyDescription;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getProviderSurname() {
        return providerSurname;
    }

    public void setProviderSurname(String providerSurname) {
        this.providerSurname = providerSurname;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getCompanyPhone() {
        return companyPhone;
    }

    public void setCompanyPhone(String companyPhone) {
        this.companyPhone = companyPhone;
    }

    public String getProviderPhone() {
        return providerPhone;
    }

    public void setProviderPhone(String providerPhone) {
        this.providerPhone = providerPhone;
    }

    public String getCompanyMainImage() {
        return companyMainImage;
    }

    public void setCompanyMainImage(String companyMainImage) {
        this.companyMainImage = companyMainImage;
    }

    public String getProviderMainImage() {
        return providerMainImage;
    }

    public void setProviderMainImage(String providerMainImage) {
        this.providerMainImage = providerMainImage;
    }

    public String getCompanyEmail() {
        return companyEmail;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
    }

    public String getProviderEmail() {
        return providerEmail;
    }

    public void setProviderEmail(String providerEmail) {
        this.providerEmail = providerEmail;
    }

    public String getCompanyDescription() {
        return companyDescription;
    }

    public void setCompanyDescription(String companyDescription) {
        this.companyDescription = companyDescription;
    }
}
