package com.example.eventplanner.activities.eventtype;

import com.example.eventplanner.model.EventType;

import java.lang.reflect.Array;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface EventTypeService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type: application/json"
    })
    @GET("event-types/all")
    Call<ArrayList<EventType>> getAll();
}
