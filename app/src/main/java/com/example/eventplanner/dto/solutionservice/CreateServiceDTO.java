package com.example.eventplanner.dto.solutionservice;

import com.example.eventplanner.enumeration.ReservationType;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class CreateServiceDTO {
    private String name;
    private String description;
    private String imageUrl;
    private Double price;
    private Double discount;
    private Long categoryId;
    private Boolean isVisible;
    private String specifics;
    private Integer reservationDeadline;
    private Integer cancellationDeadline;
    private Duration fixedTime;
    private Duration minTime;
    private Duration maxTime;
    private ReservationType reservationType;
    private List<Long> eventTypeIds;
    private String business;
    private String city;
    private Boolean availability;
    private LocalTime workingHoursStart;
    private LocalTime workingHoursEnd;
    private List<Integer> workingDays;
    private List<LocalDate> unavailableDates;

    public CreateServiceDTO() {
    }

    public CreateServiceDTO(String name, String description, String imageUrl, Double price, Double discount, Long categoryId, Boolean isVisible, String specifics, Integer reservationDeadline, Integer cancellationDeadline, Duration fixedTime, Duration minTime, Duration maxTime, ReservationType reservationType, List<Long> eventTypeIds, String business, String city, Boolean availability, LocalTime workingHoursStart, LocalTime workingHoursEnd, List<Integer> workingDays, List<LocalDate> unavailableDates) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
        this.discount = discount;
        this.categoryId = categoryId;
        this.isVisible = isVisible;
        this.specifics = specifics;
        this.reservationDeadline = reservationDeadline;
        this.cancellationDeadline = cancellationDeadline;
        this.fixedTime = fixedTime;
        this.minTime = minTime;
        this.maxTime = maxTime;
        this.reservationType = reservationType;
        this.eventTypeIds = eventTypeIds;
        this.business = business;
        this.city = city;
        this.availability = availability;
        this.workingHoursStart = workingHoursStart;
        this.workingHoursEnd = workingHoursEnd;
        this.workingDays = workingDays;
        this.unavailableDates = unavailableDates;
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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Boolean getVisible() {
        return isVisible;
    }

    public void setVisible(Boolean visible) {
        isVisible = visible;
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

    public List<Long> getEventTypeIds() {
        return eventTypeIds;
    }

    public void setEventTypeIds(List<Long> eventTypeIds) {
        this.eventTypeIds = eventTypeIds;
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

    public Boolean getAvailability() {
        return availability;
    }

    public void setAvailability(Boolean availability) {
        this.availability = availability;
    }

    public LocalTime getWorkingHoursStart() {
        return workingHoursStart;
    }

    public void setWorkingHoursStart(LocalTime workingHoursStart) {
        this.workingHoursStart = workingHoursStart;
    }

    public LocalTime getWorkingHoursEnd() {
        return workingHoursEnd;
    }

    public void setWorkingHoursEnd(LocalTime workingHoursEnd) {
        this.workingHoursEnd = workingHoursEnd;
    }

    public List<Integer> getWorkingDays() {
        return workingDays;
    }

    public void setWorkingDays(List<Integer> workingDays) {
        this.workingDays = workingDays;
    }

    public List<LocalDate> getUnavailableDates() {
        return unavailableDates;
    }

    public void setUnavailableDates(List<LocalDate> unavailableDates) {
        this.unavailableDates = unavailableDates;
    }
}
