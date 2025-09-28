package com.example.eventplanner.dto.conversation;

import java.time.LocalDateTime;
import java.util.List;

public class GetConversationDTO {
    private Long id;
    private GetChatUserDTO otherUser;
    private LocalDateTime lastUpdated;
    private List<GetChatMessageDTO> messages;
    private boolean isBlocked;

    public Long getId() { return id; }
    public GetChatUserDTO getOtherUser() { return otherUser; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public List<GetChatMessageDTO> getMessages() { return messages; }
    public boolean isBlocked() { return isBlocked; }
}

