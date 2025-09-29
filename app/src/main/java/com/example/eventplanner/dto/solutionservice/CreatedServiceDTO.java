package com.example.eventplanner.dto.solutionservice;

import com.example.eventplanner.enumeration.ReservationType;

import java.time.Duration;
import java.util.List;

public class CreatedServiceDTO {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private Double price;
    private Double discount;
    private Boolean isVisible;
    private Boolean availability;
    private Long categoryId;
    private String specifics;
    private Integer reservationDeadline;
    private Integer cancellationDeadline;
    private Duration fixedTime;
    private Duration minTime;
    private Duration maxTime;
    private ReservationType reservationType;
    private List<String> eventTypes;
    private String business;
    private String city;

    public CreatedServiceDTO() {
    }

    public CreatedServiceDTO(Long id, String name, String description, String imageUrl, Double price, Double discount, Boolean isVisible, Boolean availability, Long categoryId, String specifics, Integer reservationDeadline, Integer cancellationDeadline, Duration fixedTime, Duration minTime, Duration maxTime, ReservationType reservationType, List<String> eventTypes, String business, String city) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
        this.discount = discount;
        this.isVisible = isVisible;
        this.availability = availability;
        this.categoryId = categoryId;
        this.specifics = specifics;
        this.reservationDeadline = reservationDeadline;
        this.cancellationDeadline = cancellationDeadline;
        this.fixedTime = fixedTime;
        this.minTime = minTime;
        this.maxTime = maxTime;
        this.reservationType = reservationType;
        this.eventTypes = eventTypes;
        this.business = business;
        this.city = city;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Boolean getVisible() {
        return isVisible;
    }

    public void setVisible(Boolean visible) {
        isVisible = visible;
    }

    public Boolean getAvailability() {
        return availability;
    }

    public void setAvailability(Boolean availability) {
        this.availability = availability;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getSpecifics() {
        return specifics;
    }

    public void setSpecifics(String specifics) {
        this.specifics = specifics;
    }

    public Integer getReservationDeadline() {
        return reservationDeadline;
    }

    public void setReservationDeadline(Integer reservationDeadline) {
        this.reservationDeadline = reservationDeadline;
    }

    public Integer getCancellationDeadline() {
        return cancellationDeadline;
    }

    public void setCancellationDeadline(Integer cancellationDeadline) {
        this.cancellationDeadline = cancellationDeadline;
    }

    public Duration getFixedTime() {
        return fixedTime;
    }

    public void setFixedTime(Duration fixedTime) {
        this.fixedTime = fixedTime;
    }

    public Duration getMinTime() {
        return minTime;
    }

    public void setMinTime(Duration minTime) {
        this.minTime = minTime;
    }

    public Duration getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(Duration maxTime) {
        this.maxTime = maxTime;
    }

    public ReservationType getReservationType() {
        return reservationType;
    }

    public void setReservationType(ReservationType reservationType) {
        this.reservationType = reservationType;
    }

    public List<String> getEventTypes() {
        return eventTypes;
    }

    public void setEventTypes(List<String> eventTypes) {
        this.eventTypes = eventTypes;
    }

    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
