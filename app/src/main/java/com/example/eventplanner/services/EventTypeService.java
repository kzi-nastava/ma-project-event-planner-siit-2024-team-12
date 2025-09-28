package com.example.eventplanner.services;

import com.example.eventplanner.dto.eventtype.CreateEventTypeDTO;
import com.example.eventplanner.dto.eventtype.UpdateEventTypeDTO;
import com.example.eventplanner.dto.eventtype.GetEventTypeDTO;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface EventTypeService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type: application/json"
    })
    @GET("event-types/all")
    Call<ArrayList<GetEventTypeDTO>> getAll(@Header("Authorization") String token);


    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type: application/json"
    })
    @POST("event-types")
    Call<GetEventTypeDTO> createEventType(@Header("Authorization") String token, @Body CreateEventTypeDTO createEventTypeDTO);


    @PUT("event-types/{id}")
    Call<GetEventTypeDTO> updateEventType(@Header("Authorization") String token, @Body UpdateEventTypeDTO updateEventTypeDTO, @Path("id") Long id);


    @PUT("event-types/{id}/activate")
    Call<ResponseBody> activateEventType(@Header("Authorization") String token, @Path("id") Long id);


    @PUT("event-types/{id}/deactivate")
    Call<ResponseBody> deactivateEventType(@Header("Authorization") String token, @Path("id") Long id);


    @GET("event-types/all-active")
    Call<ArrayList<GetEventTypeDTO>> getAllActive(@Header("Authorization") String token);


    @GET("event-types/{eventTypeName}/suggested-categories")
    Call<ArrayList<String>> getSuggestedCategories(@Header("Authorization") String token,
                                                   @Path("eventTypeName") String eventTypeName);
}
