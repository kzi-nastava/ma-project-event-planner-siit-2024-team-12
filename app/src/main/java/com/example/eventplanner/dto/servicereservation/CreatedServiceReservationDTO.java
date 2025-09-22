package com.example.eventplanner.dto.servicereservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class CreatedServiceReservationDTO {

    private LocalTime timeFrom;
    private LocalTime timeTo;
    private String status;
    private String message;

    private LocalDateTime requestDateTime;

    private ServiceInfo service;
    private EventInfo event;
    private Double amount;

    public CreatedServiceReservationDTO() {}

    public static class ServiceInfo {
        private Long id;
        private String name;
        private String image;
        private Double price;
        private String business;

        public Long getId() { return id;}
        public void setId(Long id) { this.id = id;}

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getImage() { return image; }
        public void setImage(String image) { this.image = image; }

        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }

        public String getBusiness() { return business; }
        public void setBusiness(String business) { this.business = business; }
    }

    public static class EventInfo {
        private String name;
        private LocalDate date;
        private String city;
        private String locationName;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }

        public String getLocationName() { return locationName; }
        public void setLocationName(String locationName) { this.locationName = locationName; }
    }

    public LocalTime getTimeFrom() { return timeFrom; }
    public void setTimeFrom(LocalTime timeFrom) { this.timeFrom = timeFrom; }

    public LocalTime getTimeTo() { return timeTo; }
    public void setTimeTo(LocalTime timeTo) { this.timeTo = timeTo; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getRequestDateTime() { return requestDateTime; }
    public void setRequestDateTime(LocalDateTime requestDateTime) { this.requestDateTime = requestDateTime; }

    public ServiceInfo getService() { return service; }
    public void setService(ServiceInfo service) { this.service = service; }

    public EventInfo getEvent() { return event; }
    public void setEvent(EventInfo event) { this.event = event; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
}
