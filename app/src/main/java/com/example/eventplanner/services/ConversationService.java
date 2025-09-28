package com.example.eventplanner.services;

import com.example.eventplanner.dto.conversation.GetConversationDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface ConversationService {

    @GET("conversation")
    Call<List<GetConversationDTO>> getConversationsForLoggedInUser(@Header("Authorization") String authorizationHeader);
}
