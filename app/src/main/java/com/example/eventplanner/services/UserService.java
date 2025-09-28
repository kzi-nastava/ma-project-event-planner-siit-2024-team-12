package com.example.eventplanner.services;

import com.example.eventplanner.dto.event.AcceptedEventDTO;
import com.example.eventplanner.dto.event.FavEventDTO;
import com.example.eventplanner.dto.solution.FavSolutionDTO;
import com.example.eventplanner.dto.user.GetUserDTO;
import com.example.eventplanner.dto.user.UpdateUserDTO;

import com.example.eventplanner.dto.user.UpdatedUserDTO;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

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


    @GET("users/{email}/favorite-services")
    Call<ArrayList<FavSolutionDTO>> getFavoriteServices(@Header("Authorization") String token,
                                                        @Path("email") String email);


    @GET("users/{email}/favorite-products")
    Call<ArrayList<FavSolutionDTO>> getFavoriteProducts(@Header("Authorization") String token,
                                                        @Path("email") String email);


    @POST("users/{email}/favorite-event-addition")
    Call<ResponseBody> addFavoriteEvent(@Header("Authorization") String token,
                                      @Path("email") String email,
                                      @Body Long eventId);


    @GET("users/{email}/is-favorite-event")
    Call<Boolean> isEventFavorite(@Header("Authorization") String token,
                                  @Path("email") String email,
                                  @Query("eventId") Long eventId);


    @DELETE("users/{email}/remove-favorite-event")
    Call<Void> removeFavoriteEvent(@Header("Authorization") String token,
                                   @Path("email") String email,
                                   @Query("eventId") Long eventId);


    @GET("users/{email}/is-favorite-product")
    Call<Boolean> isProductFavorite(@Header("Authorization") String token,
                                    @Path("email") String email,
                                    @Query("productId") Long productId);


    @POST("users/{email}/favorite-product-addition")
    Call<ResponseBody> addFavoriteProduct(@Header("Authorization") String token,
                                          @Path("email") String email,
                                          @Body Long productId);


    @DELETE("users/{email}/remove-favorite-product")
    Call<Void> removeFavoriteProduct(@Header("Authorization") String token,
                                     @Path("email") String email,
                                     @Query("productId") Long productId);


    @POST("users/{email}/favorite-service-addition")
    Call<ResponseBody> addFavoriteService(@Header("Authorization") String token,
                                        @Path("email") String email,
                                        @Body Long serviceId);


    @GET("users/{email}/is-favorite-service")
    Call<Boolean> isServiceFavorite(@Header("Authorization") String token,
                                  @Path("email") String email,
                                  @Query("serviceId") Long serviceId);


    @DELETE("users/{email}/remove-favorite-service")
    Call<Void> removeFavoriteService(@Header("Authorization") String token,
                                   @Path("email") String email,
                                   @Query("serviceId") Long serviceId);

    @GET("users/{email}/profile")
    Call<GetUserDTO> getUserProfile(@Header("Authorization") String authorization, @Path("email") String email);

    @PATCH("users/suspend/{userId}")
    Call<ResponseBody> suspendUser(@Header("Authorization") String authorization, @Path("userId") Long userId);

    @POST("users/block/{userId}")
    Call<ResponseBody> blockUser(@Header("Authorization") String authorization, @Path("userId") Long userId);

    @DELETE("users/unblock/{userId}")
    Call<ResponseBody> unblockUser(@Header("Authorization") String authorization, @Path("userId") Long userId);

}
