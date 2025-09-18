package com.example.eventplanner.activities.service;

import com.example.eventplanner.dto.solutionservice.CreateServiceDTO;
import com.example.eventplanner.dto.solutionservice.CreatedServiceDTO;
import com.example.eventplanner.dto.solutionservice.GetServiceDTO;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ServiceSolutionService {

    @GET("services/provided")
    Call<List<GetServiceDTO>> getProvidedServices(@Header("Authorization") String token);
    @POST("services")
    Call<CreatedServiceDTO> createService(@Header("Authorization") String auth, @Body CreateServiceDTO service);

}
