package com.example.eventplanner.dto.notification;

import com.example.eventplanner.enumeration.NotificationType;

import java.time.LocalDateTime;

public class GetNotificationDTO {
    private Long id;
    private String senderEmail;
    private String content;
    private LocalDateTime createdAt;
    private NotificationType type;
    private boolean muted;
    private String entityType;
    private Long entityId;

    public GetNotificationDTO() {
    }


    public Long getId() { return id; }
    public String getSenderEmail() { return senderEmail; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public NotificationType getType() { return type; }
    public boolean isMuted() { return muted; }
    public String getEntityType() { return entityType; }
    public Long getEntityId() { return entityId; }


    public void setId(Long id) { this.id = id; }
    public void setSenderEmail(String senderEmail) { this.senderEmail = senderEmail; }
    public void setContent(String content) { this.content = content; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setType(NotificationType type) { this.type = type; }
    public void setMuted(boolean muted) { this.muted = muted; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }
}