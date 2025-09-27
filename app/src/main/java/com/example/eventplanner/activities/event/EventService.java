package com.example.eventplanner.activities.event;

import com.example.eventplanner.dto.budget.GetBudgetItemDTO;
import com.example.eventplanner.dto.budget.UpdateBudgetDTO;
import com.example.eventplanner.dto.budget.UpdateBudgetForEventDTO;
import com.example.eventplanner.dto.event.CreateEventDTO;
import com.example.eventplanner.dto.event.EventDetailsDTO;
import com.example.eventplanner.dto.event.FavEventDTO;
import com.example.eventplanner.dto.event.GetEventDTO;
import com.example.eventplanner.dto.event.UpdatedEventDTO;

import java.util.ArrayList;
import java.util.List;

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


    @GET("events/{eventId}/accepted-guests")
    Call<List<String>> getAcceptedGuests(@Header("Authorization") String token,
                                         @Path("eventId") Long eventId);

    @GET("events/explore-events")
    Call<ArrayList<FavEventDTO>> getOpenEvents(@Header("Authorization") String token);

    @GET("events/my-events")
    Call<List<GetEventDTO>> getEventsByOrganizer(
            @Header("Authorization") String auth);
    @GET("events/{eventId}/budget")
    Call<UpdateBudgetForEventDTO> getBudgetDetailsByEventId(
            @Header("Authorization") String token,
            @Path("eventId") Long eventId);
    @PUT("events/{eventId}/update-budget")
    Call<List<GetBudgetItemDTO>> updateBudget(
            @Header("Authorization") String token,
            @Body List<UpdateBudgetDTO> updateBudgetItems,
            @Path("eventId") Long eventId
    );
}

