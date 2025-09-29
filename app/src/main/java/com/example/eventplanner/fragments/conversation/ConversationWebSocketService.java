package com.example.eventplanner.fragments.conversation;

import android.util.Log;

import com.example.eventplanner.BuildConfig;
import com.example.eventplanner.adapters.datetime.DurationAdapter;
import com.example.eventplanner.adapters.datetime.LocalDateAdapter;
import com.example.eventplanner.adapters.datetime.LocalDateTimeAdapter;
import com.example.eventplanner.adapters.datetime.LocalTimeAdapter;
import com.example.eventplanner.dto.conversation.GetChatMessageDTO;
import com.example.eventplanner.dto.conversation.GetConversationDTO;
import com.example.eventplanner.dto.notification.GetNotificationDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;

public class ConversationWebSocketService {

    private static final String TAG = "Conversation WebSocketService";
    private StompClient stompClient;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final List<ConversationCountListener> listeners = new ArrayList<>();
    private int unreadCount = 0;

    public interface ConversationCountListener {
        void onUnreadCountChanged(int newCount);
    }

    public void addListener(ConversationCountListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(ConversationCountListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (ConversationCountListener listener : listeners) {
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
    private void subscribeToMessages() {
        Log.d(TAG, "subscribeToMessages() called");

        Disposable disposable = stompClient.topic("/user/queue/messages")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stompMessage -> {
                    Log.d(TAG, "Received message: " + stompMessage.getPayload());
                    unreadCount++;
                    notifyListeners();
                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                            .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
                            .registerTypeAdapter(Duration.class, new DurationAdapter())
                            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                            .create();
                    GetConversationDTO dto = gson.fromJson(stompMessage.getPayload(), GetConversationDTO.class);
                    List<GetChatMessageDTO> mssgs = dto.getMessages();
                    notifyListener(mssgs);
                }, throwable -> {
                    Log.e(TAG, "Error on topic subscription", throwable);
                });

        compositeDisposable.add(disposable);
        Log.d(TAG, "Subscribed to /user/queue/messages");
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

        String url = "ws://" + BuildConfig.IP_ADDR+ ":8080/ws/websocket";

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", jwtToken);

        Log.d(TAG, "Attempting to connect to: " + url);
        Log.d(TAG, "With headers: " + headers.toString());

        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url, headers);

        compositeDisposable.add(stompClient.lifecycle().subscribe(lifecycleEvent -> {
            switch (lifecycleEvent.getType()) {
                case OPENED:
                    Log.d(TAG, "Connected to WebSocket. Now subscribing...");
                    subscribeToMessages();
                    break;
                case ERROR:
                    Log.e(TAG, "STOMP error", lifecycleEvent.getException());
                    break;
                case CLOSED:
                    Log.w(TAG, "WebSocket connection closed.");
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


    public interface ConversationListener {
        void onNotificationReceived(List<GetChatMessageDTO> notifications);
    }

    private final List<ConversationListener> ConversationListeners = new ArrayList<>();

    public void addConversationListener(ConversationListener listener) {
        ConversationListeners.add(listener);
    }

    private void notifyListener(List<GetChatMessageDTO> messages) {
        for (ConversationListener l : ConversationListeners) {
            l.onNotificationReceived(messages);
        }
    }


}