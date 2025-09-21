package com.example.eventplanner.activities.solutioncategory;

import com.example.eventplanner.dto.solutioncategory.GetCategoryDTO;
import com.example.eventplanner.dto.solutioncategory.GetSolutionCategoryDTO;
import com.example.eventplanner.dto.solutioncategory.UpdateCategoryDTO;
import com.example.eventplanner.dto.solutioncategory.UpdatedCategoryDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface SolutionCategoryService {
    @GET("categories/accepted")
    Call<List<GetSolutionCategoryDTO>> getAllAccepted(@Header("Authorization") String token);
    @GET("categories/recommended")
    Call<List<GetCategoryDTO>> getAllRecommended(@Header("Authorization") String token);
    @GET("categories/accepted")
    Call<List<GetCategoryDTO>> getAccepted(@Header("Authorization") String token);
    @PUT("categories/{id}")
    Call<UpdatedCategoryDTO> updateCategory(
            @Header("Authorization") String authHeader,
            @Path("id") Long id,
            @Body UpdateCategoryDTO updateCategoryDTO
    );
    @DELETE("categories/{id}")
    Call<Void> deleteCategory(
            @Header("Authorization") String authHeader,
            @Path("id") Long id
    );
}
