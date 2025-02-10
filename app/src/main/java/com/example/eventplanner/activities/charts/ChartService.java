package com.example.eventplanner.activities.charts;

import com.example.eventplanner.dto.charts.EventAttendanceDTO;
import com.example.eventplanner.dto.charts.EventRatingsDTO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface ChartService {

    @GET("events/attendance")
    Call<ArrayList<EventAttendanceDTO>> getEventAttendance(@Header("Authorization") String token);


    @GET("events/ratings")
    Call<ArrayList<EventRatingsDTO>> getEventRatings(@Header("Authorization") String token);
}
