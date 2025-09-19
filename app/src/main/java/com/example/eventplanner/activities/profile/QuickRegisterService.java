package com.example.eventplanner.activities.profile;


import com.example.eventplanner.dto.event.GetEventDTO;
import com.example.eventplanner.dto.user.CreatedUserDTO;
import com.example.eventplanner.dto.user.GetUserDTO;
import com.example.eventplanner.dto.user.QuickRegisteredUserDTO;
import com.example.eventplanner.dto.user.QuickRegisteredUserResponseDTO;
import com.example.eventplanner.dto.user.UpgradeUserDTO;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface QuickRegisterService {

    @GET("quick-register/validate-token")
    Call<ResponseBody> validateInvitationToken(@Query("token") String token);

    @POST("quick-register/register")
    Call<QuickRegisteredUserResponseDTO> quickRegister(@Query("token") String token, @Body QuickRegisteredUserDTO dto);

    @GET("quick-register/home")
    @Headers("Content-Type: application/json")
    Call<ResponseBody> home(@Header("Authorization") String authorization);

    @GET("quick-register/profile")
    @Headers("Content-Type: application/json")
    Call<GetUserDTO> profile(@Header("Authorization") String authorization);

    @POST("quick-register/upgrade-role")
    @Headers("Content-Type: application/json")
    Call<CreatedUserDTO> upgradeRole(@Header("Authorization") String authorization, @Body UpgradeUserDTO dto);

    @GET("quick-register/invited-events")
    Call<List<GetEventDTO>> getInvitedEvents(
            @Header("Authorization") String authorization,
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("quick-register/extract-email")
    Call<ResponseBody> extractEmailFromToken(@Query("token") String token);
}
