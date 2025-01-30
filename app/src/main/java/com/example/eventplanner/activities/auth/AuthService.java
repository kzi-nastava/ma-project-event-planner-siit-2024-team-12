package com.example.eventplanner.activities.auth;

import com.example.eventplanner.dto.auth.LogInRequest;
import com.example.eventplanner.dto.auth.PasswordChangeRequest;
import com.example.eventplanner.dto.auth.UserTokenState;
import com.example.eventplanner.dto.user.CreateUserDTO;
import com.example.eventplanner.dto.user.GetUserDTO;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AuthService {
    @Headers({
            "User-Agent: Mobile-Android",
    })
    @POST("auth/signup")
    Call<ResponseBody> registerUser(@Body CreateUserDTO createUserDTO);

    @GET("auth/activate-account")
    Call<ResponseBody> activateUserAccount(@Query("email") String email);

    @GET("auth/verify-account")
    Call<ResponseBody> verifyUserAccount(@Query("email") String email);

    @POST("auth/login")
    Call<UserTokenState> logIn(@Body LogInRequest request);

    @GET("auth/current-user")
    Call<GetUserDTO> getCurrentUser(@Header("Authorization") String token);


    @POST("auth/password-change")
    Call<ResponseBody> changePassword(@Header("Authorization") String token, @Body PasswordChangeRequest request);
}

