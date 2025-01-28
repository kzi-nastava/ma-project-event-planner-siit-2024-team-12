package com.example.eventplanner.activities.auth;

import com.example.eventplanner.dto.user.CreateUserDTO;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {
    @POST("auth/signup")
    Call<ResponseBody> registerUser(@Body CreateUserDTO createUserDTO);
}

