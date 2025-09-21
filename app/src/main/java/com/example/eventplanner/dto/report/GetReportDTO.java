package com.example.eventplanner.dto.report;

import java.time.LocalDateTime;

public class GetReportDTO {

    private Long id;
    private String reportedUserEmail;
    private String reportedByEmail;
    private Long reportedUserId;
    private Long reportedById;
    private String reason;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getReportedUserEmail() {
        return reportedUserEmail;
    }

    public void setReportedUserEmail(String reportedUserEmail) {
        this.reportedUserEmail = reportedUserEmail;
    }

    public String getReportedByEmail() {
        return reportedByEmail;
    }

    public void setReportedByEmail(String reportedByEmail) {
        this.reportedByEmail = reportedByEmail;
    }

    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getCreatedAt() {return createdAt;}
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}

    public Long getReportedUserId() {return reportedUserId;}
    public void setReportedUserId(Long reportedUserId) {this.reportedUserId = reportedUserId;}

    public Long getReportedById() {return reportedById;}
    public void setReportedById(Long reportedById) {this.reportedById = reportedById;}
}
