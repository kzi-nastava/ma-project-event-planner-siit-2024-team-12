package com.example.eventplanner.fragments.notification;


import android.util.Log;

import com.example.eventplanner.adapters.datetime.DurationAdapter;
import com.example.eventplanner.adapters.datetime.LocalDateAdapter;
import com.example.eventplanner.adapters.datetime.LocalDateTimeAdapter;
import com.example.eventplanner.adapters.datetime.LocalTimeAdapter;
import com.example.eventplanner.dto.notification.GetNotificationDTO;
import com.example.eventplanner.utils.ClientUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;

public class NotificationWebSocketService {

    private static final String TAG = "WebSocketService";
    private StompClient stompClient;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final List<NotificationCountListener> listeners = new ArrayList<>();
    private int unreadCount = 0;

    public interface NotificationCountListener {
        void onUnreadCountChanged(int newCount);
    }

    public void addListener(NotificationCountListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(NotificationCountListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (NotificationCountListener listener : listeners) {
            listener.onUnreadCountChanged(unreadCount);
        }
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void markAllAsRead() {
        unreadCount = 0;
        notifyListeners();
    }

    // Pretplata na notifikacije
    private void subscribeToNotifications() {
        Log.d(TAG, "📡 subscribeToNotifications() called");

        Disposable disposable = stompClient.topic("/user/queue/notifications")
                .subscribe(stompMessage -> {
                    Log.d(TAG, "✅ Received message: " + stompMessage.getPayload());
                    unreadCount++;
                    notifyListeners();
                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                            .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
                            .registerTypeAdapter(Duration.class, new DurationAdapter())
                            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                            .create();
                    GetNotificationDTO dto = gson.fromJson( stompMessage.getPayload(), GetNotificationDTO.class);
                    notifyListener(dto);
                }, throwable -> {
                    Log.e(TAG, "❌ Error on topic subscription", throwable);
                });

        compositeDisposable.add(disposable);
        Log.d(TAG, "📡 Subscribed to /user/queue/notifications");
    }

    public void connect(String jwtToken) {
        if (stompClient != null && stompClient.isConnected()) {
            Log.d(TAG, "WebSocket is already connected.");
            return;
        }

        if (jwtToken == null || jwtToken.isEmpty()) {
            Log.e(TAG, "JWT token is missing or empty. Cannot connect to WebSocket.");
            return;
        }

        String url = "ws://192.168.100.26:8080/ws/websocket";

        // Authorization header
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", jwtToken);

        Log.d(TAG, "Attempting to connect to: " + url);
        Log.d(TAG, "With headers: " + headers.toString());

        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url, headers);

        compositeDisposable.add(stompClient.lifecycle().subscribe(lifecycleEvent -> {
            switch (lifecycleEvent.getType()) {
                case OPENED:
                    Log.d(TAG, "🌐 Connected to WebSocket. Now subscribing...");
                    subscribeToNotifications();
                    break;
                case ERROR:
                    Log.e(TAG, "⚠️ STOMP error", lifecycleEvent.getException());
                    break;
                case CLOSED:
                    Log.w(TAG, "🔌 WebSocket connection closed.");
                    break;
            }
        }));
        List<StompHeader> header = new ArrayList<>();
        header.add(new StompHeader("Authorization", jwtToken));
        stompClient.connect(header);
    }

    public void disconnect() {
        if (stompClient != null) {
            stompClient.disconnect();
            compositeDisposable.dispose();
            Log.d(TAG, "Disconnected from WebSocket.");
        }
    }


    public interface NotificationListener {
        void onNotificationReceived(GetNotificationDTO notification);
    }

    private final List<NotificationListener> notificationListeners = new ArrayList<>();

    public void addNotificationListener(NotificationListener listener) {
        notificationListeners.add(listener);
    }

    private void notifyListener(GetNotificationDTO notification) {
        for (NotificationListener l : notificationListeners) {
            l.onNotificationReceived(notification);
        }
    }


}