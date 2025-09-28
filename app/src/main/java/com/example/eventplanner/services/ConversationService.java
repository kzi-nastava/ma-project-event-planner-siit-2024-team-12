package com.example.eventplanner.services;

import com.example.eventplanner.dto.conversation.GetChatMessageDTO;
import com.example.eventplanner.dto.conversation.GetConversationDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ConversationService {

    @GET("conversation")
    Call<List<GetConversationDTO>> getConversationsForLoggedInUser(@Header("Authorization") String authorizationHeader);

    @PUT("conversation/{id}")
    Call<GetConversationDTO> markAllAsRead(
            @Header("Authorization") String authorizationHeader,
            @Path("id") Long conversationId
    );

    @POST(value = "conversation/{conversationId}")
    Call<GetChatMessageDTO> sendMessage(
            @Header("Authorization") String authorizationHeader,
            @Path("conversationId") Long conversationId,
            @Body String messageContent
    );

    @PUT("conversation/{conversationId}")
    Call<GetConversationDTO> markAllMessagesAsRead(
            @Header("Authorization") String authorizationHeader,
            @Path("conversationId") Long conversationId
    );
}
