package com.example.eventplanner.services;

import com.example.eventplanner.dto.conversation.GetChatMessageDTO;
import com.example.eventplanner.dto.conversation.GetConversationDTO;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
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
            @Body RequestBody messageContent
    );

    @GET("conversation/{solutionType}/{solutionId}")
    Call<Long> getConversationIdForSolutionOwner(
            @Header("Authorization") String authorization,
            @Path("solutionType") String type,
            @Path("solutionId") Long solutionId
    );
    @GET("conversation/{eventId}")
    Call<Long> getConversationIdForEventOwner(
            @Header("Authorization") String authorization,
            @Path("eventId") Long eventId
    );
}
