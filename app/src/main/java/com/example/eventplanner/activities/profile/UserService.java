package com.example.eventplanner.activities.profile;

import com.example.eventplanner.dto.user.UpdateUserDTO;

import com.example.eventplanner.dto.user.UpdatedUserDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserService {
    @PUT("users/{email}")
    Call<UpdatedUserDTO> update(@Header("Authorization") String token, @Path("email") String email, @Body UpdateUserDTO updateUserDTO);
}
