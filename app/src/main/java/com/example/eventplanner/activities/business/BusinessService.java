package com.example.eventplanner.activities.business;

import com.example.eventplanner.dto.business.CreateBusinessDTO;
import com.example.eventplanner.dto.business.GetBusinessDTO;
import com.example.eventplanner.dto.business.UpdateBusinessDTO;
import com.example.eventplanner.dto.business.UpdatedBusinessDTO;
import com.example.eventplanner.model.GetEventTypeDTO;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface BusinessService {
    @POST("businesses")
    Call<ResponseBody> registerBusiness(@Header("Authorization") String token, @Body CreateBusinessDTO dto);

    @GET("businesses/current")
    Call<GetBusinessDTO> getBusinessForCurrentUser(@Header("Authorization") String authorization);

    @DELETE("businesses/{companyEmail}")
    Call<ResponseBody> deactivateBusiness(@Header("Authorization") String token,
                                          @Path("companyEmail") String email);


    @PUT("businesses/{companyEmail}")
    Call<UpdatedBusinessDTO> update(@Header("Authorization") String token, @Path("companyEmail") String email,
                                    @Body UpdateBusinessDTO dto);


    @GET("businesses/{companyEmail}/event-types")
    Call<ArrayList<GetEventTypeDTO>> getEventTypesByBusiness(@Header("Authorization") String token,
                                                             @Path("companyEmail") String email);
}
