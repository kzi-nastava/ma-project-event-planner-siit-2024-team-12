package com.example.eventplanner.dto.charts;

public class EventAttendanceDTO {
    private String eventName;
    private Integer maxGuests;
    private Integer attendance;
    private Double percentage;

    public EventAttendanceDTO(String eventName, Integer maxGuests, Integer attendance, Double percentage) {
        this.eventName = eventName;
        this.maxGuests = maxGuests;
        this.attendance = attendance;
        this.percentage = percentage;
    }


    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public Integer getMaxGuests() { return maxGuests; }
    public void setMaxGuests(Integer maxGuests) { this.maxGuests = maxGuests; }

    public Integer getAttendance() { return attendance; }
    public void setAttendance(Integer attendance) { this.attendance = attendance; }

    public Double getPercentage() { return percentage; }
    public void setPercentage(Double percentage) { this.percentage = percentage; }

}
