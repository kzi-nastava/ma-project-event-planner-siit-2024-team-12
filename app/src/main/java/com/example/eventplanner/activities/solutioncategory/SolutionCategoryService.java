package com.example.eventplanner.activities.solutioncategory;

import com.example.eventplanner.dto.solutioncategory.GetCategoryDTO;
import com.example.eventplanner.dto.solutioncategory.GetSolutionCategoryDTO;
import com.example.eventplanner.dto.solutioncategory.UpdateCategoryDTO;
import com.example.eventplanner.dto.solutioncategory.UpdatedCategoryDTO;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SolutionCategoryService {
    @GET("categories/accepted")
    Call<List<GetSolutionCategoryDTO>> getAllAccepted(@Header("Authorization") String token);
    @GET("categories/recommended")
    Call<List<GetCategoryDTO>> getAllRecommended(@Header("Authorization") String token);
    @GET("categories/accepted")
    Call<List<GetCategoryDTO>> getAccepted(@Header("Authorization") String token);
    @PUT("categories/{id}")
    Call<UpdatedCategoryDTO> updateCategory(
            @Header("Authorization") String token,
            @Path("id") Long id,
            @Body UpdateCategoryDTO updateCategoryDTO
    );
    @DELETE("categories/{id}")
    Call<Void> deleteCategory(
            @Header("Authorization") String token,
            @Path("id") Long id
    );
    @PUT("categories/approve/{id}")
    Call<UpdatedCategoryDTO> approveCategory(
            @Header("Authorization") String token,
            @Path("id") Long id,
            @Body UpdateCategoryDTO category
    );
    @PUT("categories/disapprove/{id}")
    Call<UpdatedCategoryDTO> disapproveCategory(
            @Header("Authorization") String token,
            @Path("id") Long id,
            @Query("changeCategoryName") String changeCategoryName,
            @Body RequestBody emptyBody
    );
}
