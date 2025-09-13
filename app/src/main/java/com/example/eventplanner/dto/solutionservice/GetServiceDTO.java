package com.example.eventplanner.dto.solutionservice;

import com.example.eventplanner.dto.eventtype.GetEventTypeDTO;
import com.example.eventplanner.enumeration.ReservationType;

import java.time.Duration;
import java.util.List;

public class GetServiceDTO {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private Double price;
    private Double discount;
    private String category;
    private String city;
    private Boolean isVisible;
    private Boolean isAvailable;
    private String specifics;
    private Integer reservationDeadline;
    private Integer cancellationDeadline;
    private Duration minTime;
    private Duration fixedTime;
    private Duration maxTime;
    private ReservationType reservationType;
    private List<GetEventTypeDTO> eventTypes;
    private boolean isExpanded;

    public GetServiceDTO() {
    }

    public GetServiceDTO(Long id, String name, String description, String imageUrl, Double price, Double discount, String category, String city, Boolean isVisible, Boolean isAvailable, String specifics, Integer reservationDeadline, Integer cancellationDeadline, Duration minTime, Duration fixedTime, Duration maxTime, ReservationType reservationType) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
        this.discount = discount;
        this.category = category;
        this.city = city;
        this.isVisible = isVisible;
        this.isAvailable = isAvailable;
        this.specifics = specifics;
        this.reservationDeadline = reservationDeadline;
        this.cancellationDeadline = cancellationDeadline;
        this.minTime = minTime;
        this.fixedTime = fixedTime;
        this.maxTime = maxTime;
        this.reservationType = reservationType;
        this.isExpanded=false;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Boolean getVisible() {
        return isVisible;
    }

    public void setVisible(Boolean visible) {
        isVisible = visible;
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

    public Long getFixedTime() {
        return fixedTime.toMinutes();
    }

    public void setFixedTime(Duration fixedTime) {
        this.fixedTime = fixedTime;
    }

    public Long getMaxTime() {
        return maxTime.toHours();
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

    public Long getMinTime() {
        return minTime.toHours();
    }

    public void setMinTime(Duration minTime) {
        this.minTime = minTime;
    }

    public List<GetEventTypeDTO> getEventTypes() {
        return eventTypes;
    }

    public void setEventTypes(List<GetEventTypeDTO> eventTypes) {
        this.eventTypes = eventTypes;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}
