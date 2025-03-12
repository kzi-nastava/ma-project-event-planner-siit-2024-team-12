package com.example.eventplanner.activities.event;

import com.example.eventplanner.dto.event.CreateEventDTO;
import com.example.eventplanner.dto.event.EventDetailsDTO;
import com.example.eventplanner.dto.event.UpdatedEventDTO;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface EventService {
    @POST("events")
    Call<ResponseBody> createEvent(@Header("Authorization") String token, @Body CreateEventDTO dto);


    @GET("events/{id}")
    Call<EventDetailsDTO> getEvent(@Header("Authorization") String token, @Path("id") Long id);

    @GET("events/find-by-name")
    Call<EventDetailsDTO> findByName(@Header("Authorization") String token, @Query("eventName") String name);


    @PUT("events/{id}")
    Call<UpdatedEventDTO> updateEvent(@Header("Authorization") String token,
                                      @Path("id") Long eventId,
                                      @Body EventDetailsDTO updateEventDTO);
}

