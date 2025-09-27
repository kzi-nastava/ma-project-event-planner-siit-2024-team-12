package com.example.eventplanner.dto.servicereservation;


import org.jetbrains.annotations.NotNull;

import java.time.LocalTime;

public class CreateServiceReservationDTO {

    private Long serviceId;

    private LocalTime requestedTimeFrom;

    private LocalTime requestedTimeTo;

    private Long eventId;

    public CreateServiceReservationDTO() {
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public LocalTime getRequestedTimeFrom() {
        return requestedTimeFrom;
    }

    public void setRequestedTimeFrom(LocalTime requestedTimeFrom) {
        this.requestedTimeFrom = requestedTimeFrom;
    }

    public LocalTime getRequestedTimeTo() {
        return requestedTimeTo;
    }

    public void setRequestedTimeTo(LocalTime requestedTimeTo) {
        this.requestedTimeTo = requestedTimeTo;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
}

