package com.example.eventplanner.dto.servicereservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class GetServiceReservationDTO {

    private Long reservationId;
    private String serviceName;
    private String serviceDate;
    private Integer cancellationDeadline;
    private LocalTime timeFrom;
    private LocalTime timeTo;
    private String status;
    private String message;
    private Double amount;
    private LocalDateTime requestDateTime;
    private Long serviceId;

    // Event info
    private Long eventId;
    private String eventName;
    private String location;
    private String eventDate;

    private LocalDate eventLocalDate;

    private LocalDate serviceLocalDate;

    public LocalDate getServiceLocalDate() { return serviceLocalDate; }
    public void setServiceLocalDate(LocalDate serviceLocalDate) { this.serviceLocalDate = serviceLocalDate; }


    public LocalDate getEventLocalDate() { return eventLocalDate; }
    public void setEventLocalDate(LocalDate eventLocalDate) { this.eventLocalDate = eventLocalDate; }

    public GetServiceReservationDTO() {
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(String serviceDate) {
        this.serviceDate = serviceDate;
    }

    public LocalTime getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(LocalTime timeFrom) {
        this.timeFrom = timeFrom;
    }

    public LocalTime getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(LocalTime timeTo) {
        this.timeTo = timeTo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getRequestDateTime() {
        return requestDateTime;
    }

    public void setRequestDateTime(LocalDateTime requestDateTime) {
        this.requestDateTime = requestDateTime;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getCancellationDeadline() {
        return cancellationDeadline;
    }

    public void setCancellationDeadline(Integer cancellationDeadline) {
        this.cancellationDeadline = cancellationDeadline;}

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getEventDate() { return eventDate; }
    public void setEventDate(String eventDateString) { this.eventDate = eventDateString; }
}
