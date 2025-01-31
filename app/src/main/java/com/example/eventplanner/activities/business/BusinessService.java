package com.example.eventplanner.activities.business;

import com.example.eventplanner.dto.business.CreateBusinessDTO;
import com.example.eventplanner.dto.business.GetBusinessDTO;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface BusinessService {
    @POST("businesses")
    Call<ResponseBody> registerBusiness(@Header("Authorization") String token, @Body CreateBusinessDTO dto);

    @GET("businesses/current")
    Call<GetBusinessDTO> getBusinessForCurrentUser(@Header("Authorization") String authorization);
}
