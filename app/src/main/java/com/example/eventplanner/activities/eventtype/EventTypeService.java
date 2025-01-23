package com.example.eventplanner.activities.eventtype;

import com.example.eventplanner.dto.eventtype.CreateEventTypeDTO;
import com.example.eventplanner.dto.eventtype.UpdateEventTypeDTO;
import com.example.eventplanner.model.EventType;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
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
    Call<ArrayList<EventType>> getAll();


    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type: application/json"
    })
    @POST("event-types")
    Call<EventType> createEventType(@Body CreateEventTypeDTO createEventTypeDTO);


    @PUT("event-types/{id}")
    Call<EventType> updateEventType(@Body UpdateEventTypeDTO updateEventTypeDTO, @Path("id") Long id);


    @PUT("event-types/{id}/activate")
    Call<ResponseBody> activateEventType(@Path("id") Long id);


    @PUT("event-types/{id}/deactivate")
    Call<ResponseBody> deactivateEventType(@Path("id") Long id);
}
