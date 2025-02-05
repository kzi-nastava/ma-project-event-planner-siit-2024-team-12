package com.example.eventplanner.activities.solutioncategory;

import com.example.eventplanner.dto.solutioncategory.GetSolutionCategoryDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface SolutionCategoryService {
    @GET("categories/accepted")
    Call<List<GetSolutionCategoryDTO>> getAllAccepted(@Header("Authorization") String token);
}
