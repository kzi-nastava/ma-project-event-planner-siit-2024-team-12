package com.example.eventplanner.dto.solutionservice;

import com.example.eventplanner.enumeration.ReservationType;

import java.time.Duration;
import java.util.List;

public class UpdateServiceDTO {
    private String name;
    private String description;
    private String imageUrl;
    private Double price;
    private Double discount;
    private Boolean isVisible;
    private String category;
    private Boolean isAvailable;
    private String specifics;
    private Integer reservationDeadline;
    private Integer cancellationDeadline;
    private Duration fixedTime;
    private Duration maxTime;
    private Duration minTime;
    private ReservationType reservationType;
    private List<Long> eventTypeIds;

    public UpdateServiceDTO() {
    }

    public UpdateServiceDTO(String name, String description, String imageUrl, Double price, Double discount, Boolean isVisible, String category, Boolean isAvailable, String specifics, Integer reservationDeadline, Integer cancellationDeadline, Duration fixedTime, Duration maxTime, Duration minTime, ReservationType reservationType, List<Long> eventTypeIds) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
        this.discount = discount;
        this.isVisible = isVisible;
        this.category = category;
        this.isAvailable = isAvailable;
        this.specifics = specifics;
        this.reservationDeadline = reservationDeadline;
        this.cancellationDeadline = cancellationDeadline;
        this.fixedTime = fixedTime;
        this.maxTime = maxTime;
        this.minTime = minTime;
        this.reservationType = reservationType;
        this.eventTypeIds = eventTypeIds;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Boolean getAvailable() {
        return isAvailable;
    }

    public void setAvailable(Boolean available) {
        isAvailable = available;
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

    public Duration getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(Duration maxTime) {
        this.maxTime = maxTime;
    }

    public Duration getMinTime() {
        return minTime;
    }

    public void setMinTime(Duration minTime) {
        this.minTime = minTime;
    }

    public ReservationType getReservationType() {
        return reservationType;
    }

    public void setReservationType(ReservationType reservationType) {
        this.reservationType = reservationType;
    }

    public List<Long> getEventTypeIds() {
        return eventTypeIds;
    }

    public void setEventTypeIds(List<Long> eventTypeIds) {
        this.eventTypeIds = eventTypeIds;
    }
}
