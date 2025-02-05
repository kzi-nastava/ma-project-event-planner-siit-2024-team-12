package com.example.eventplanner.activities.event;

import com.example.eventplanner.dto.event.CreateEventDTO;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface EventService {
    @POST("events")
    Call<ResponseBody> createEvent(@Header("Authorization") String token, @Body CreateEventDTO dto);

}

