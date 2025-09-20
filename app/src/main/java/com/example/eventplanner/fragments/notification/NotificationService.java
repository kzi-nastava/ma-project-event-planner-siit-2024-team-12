package com.example.eventplanner.fragments.notification;

import com.example.eventplanner.dto.notification.GetNotificationDTO;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface NotificationService {

    @GET("notifications/filtered")
    Call<List<GetNotificationDTO>> getFilteredNotifications(@Header("Authorization") String token);

    @POST("notifications/mute")
    Call<Void> muteNotifications(@Header("Authorization") String token, @Query("minutes") Long minutes);

    @DELETE("notifications/mute")
    Call<Void> unmuteNotifications(@Header("Authorization") String token);

    @GET("notifications/mute")
    Call<String> getMuteStatus(@Header("Authorization") String token);
}
