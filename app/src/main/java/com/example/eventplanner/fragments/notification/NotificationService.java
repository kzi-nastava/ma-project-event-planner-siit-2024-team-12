package com.example.eventplanner.fragments.notification;

import com.example.eventplanner.dto.notification.GetNotificationDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface NotificationService {

    @GET("notifications/filtered")
    Call<List<GetNotificationDTO>> getFilteredNotifications(@Header("Authorization") String token);
}
