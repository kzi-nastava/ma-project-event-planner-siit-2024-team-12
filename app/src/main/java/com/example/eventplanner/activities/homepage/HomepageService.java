package com.example.eventplanner.activities.homepage;

import com.example.eventplanner.dto.event.GetEventDTO;
import com.example.eventplanner.dto.solution.GetHomepageSolutionDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface HomepageService {

    @GET("homepage/events/top")
    Call<List<GetEventDTO>> getTop5Events(@Header("Authorization") String bearer);

    @GET("homepage/solutions/top")
    Call<List<GetHomepageSolutionDTO>> getTop5Solutions(@Header("Authorization") String bearer);

    @GET("homepage/events/filter")
    Call<List<GetEventDTO>> searchEvents(
            @Query("name") String name,
            @Query("description") String description,
            @Query("eventType") String eventType,
            @Query("maxGuests") Integer maxGuests,
            @Query("country") String country,
            @Query("address") String address,
            @Query("city") String city,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate,
            @Query("attendance") Integer attendance,
            @Query("rating") Integer rating,
            @Query("sortBy") String sortBy,
            @Query("sortDir") String sortDir,
            @Query("page") int page,
            @Query("size") int size,
            @Query("limitTo10") boolean limitTo10,
            @Query("ignoreCityFilter") boolean ignoreCityFilter
    );
}
