package com.example.eventplanner.activities.solutioncategory;

import com.example.eventplanner.dto.solutioncategory.GetCategoryDTO;
import com.example.eventplanner.dto.solutioncategory.GetSolutionCategoryDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface SolutionCategoryService {
    @GET("categories/accepted")
    Call<List<GetSolutionCategoryDTO>> getAllAccepted(@Header("Authorization") String token);
    @GET("categories/recommended")
    Call<List<GetCategoryDTO>> getAllRecommended(@Header("Authorization") String token);
    @GET("categories/accepted")
    Call<List<GetCategoryDTO>> getAccepted(@Header("Authorization") String token);
}
