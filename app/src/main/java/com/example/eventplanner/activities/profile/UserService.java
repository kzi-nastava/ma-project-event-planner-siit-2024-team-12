package com.example.eventplanner.activities.profile;

import com.example.eventplanner.dto.event.AcceptedEventDTO;
import com.example.eventplanner.dto.event.FavEventDTO;
import com.example.eventplanner.dto.solution.FavSolutionDTO;
import com.example.eventplanner.dto.user.UpdateUserDTO;

import com.example.eventplanner.dto.user.UpdatedUserDTO;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface UserService {
    @PUT("users/{email}")
    Call<UpdatedUserDTO> update(@Header("Authorization") String token,
                                @Path("email") String email,
                                @Body UpdateUserDTO updateUserDTO);

    @DELETE("users/{email}")
    Call<ResponseBody> deleteUser(@Header("Authorization") String token,
                                  @Path("email") String email);


    @GET("users/{email}/accepted-events")
    Call<ArrayList<AcceptedEventDTO>> getAcceptedEvents(@Header("Authorization") String token,
                                                        @Path("email") String email);



    @GET("users/{email}/created-events")
    Call<ArrayList<AcceptedEventDTO>> getCreatedEvents(@Header("Authorization") String token,
                                                       @Path("email") String email);


    @GET("users/{email}/favorite-events")
    Call<ArrayList<FavEventDTO>> getFavoriteEvents(@Header("Authorization") String token,
                                                   @Path("email") String email);


    @GET("users/explore-events")
    Call<ArrayList<FavEventDTO>> getOpenEvents(@Header("Authorization") String token);


    @GET("users/{email}/favorite-services")
    Call<ArrayList<FavSolutionDTO>> getFavoriteServices(@Header("Authorization") String token,
                                                        @Path("email") String email);


    @GET("users/{email}/favorite-products")
    Call<ArrayList<FavSolutionDTO>> getFavoriteProducts(@Header("Authorization") String token,
                                                        @Path("email") String email);
}
