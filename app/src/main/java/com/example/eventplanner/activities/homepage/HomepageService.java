package com.example.eventplanner.activities.homepage;

import com.example.eventplanner.dto.event.GetEventDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface HomepageService {

    @GET("homepage/events/top")
    Call<List<GetEventDTO>> getTop5Events(@Header("Authorization") String bearer);
}
