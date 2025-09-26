package com.example.eventplanner.fragments.notification;
//cetvrti

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;
import ua.naiksoftware.stomp.dto.StompMessage;

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
}






//treci pokusaj
//
//import android.util.Log;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import io.reactivex.disposables.CompositeDisposable;
//import io.reactivex.disposables.Disposable;
//import ua.naiksoftware.stomp.Stomp;
//import ua.naiksoftware.stomp.StompClient;
//import ua.naiksoftware.stomp.dto.StompMessage;
//
//public class NotificationWebSocketService {
//
//    private static final String TAG = "WebSocketService";
//    private StompClient stompClient;
//
//    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
//    private List<NotificationCountListener> listeners = new ArrayList<>();
//    private int unreadCount = 0;
//
//    public interface NotificationCountListener {
//        void onUnreadCountChanged(int newCount);
//    }
//
//    public void addListener(NotificationCountListener listener) {
//        if (!listeners.contains(listener)) {
//            listeners.add(listener);
//        }
//    }
//
//    public void removeListener(NotificationCountListener listener) {
//        listeners.remove(listener);
//    }
//
//    private void notifyListeners() {
//        for (NotificationCountListener listener : listeners) {
//            listener.onUnreadCountChanged(unreadCount);
//        }
//    }
//
//    public int getUnreadCount() {
//        return unreadCount;
//    }
//
//    public void markAllAsRead() {
//        unreadCount = 0;
//        notifyListeners();
//    }
//
//    /**
//     * Pretplata na /user/queue/notifications
//     */
//    private void subscribeToNotifications() {
//        Log.d(TAG, "📡 subscribeToNotifications() called");
//
//        Disposable disposable = stompClient.topic("/user/queue/notifications")
//                .subscribe(stompMessage -> {
//                    Log.d(TAG, "✅ Received message: " + stompMessage.getPayload());
//                    unreadCount++;
//                    notifyListeners();
//                }, throwable -> {
//                    Log.e(TAG, "❌ Error on topic subscription", throwable);
//                });
//
//        compositeDisposable.add(disposable);
//        Log.d(TAG, "📡 Subscribed to /user/queue/notifications");
//    }
//
//    /**
//     * Konekcija na WebSocket
//     */
//    public void connect(String jwtToken) {
//        if (stompClient != null && stompClient.isConnected()) {
//            Log.d(TAG, "WebSocket is already connected.");
//            return;
//        }
//
//        if (jwtToken == null || jwtToken.isEmpty()) {
//            Log.e(TAG, "JWT token is missing or empty. Cannot connect to WebSocket.");
//            return;
//        }
//
//        String url = "ws://192.168.100.26:8080/ws/websocket";
//
//        Map<String, String> headers = new HashMap<>();
//        headers.put("Authorization", jwtToken);
//
//        Log.d(TAG, "Attempting to connect to: " + url);
//        Log.d(TAG, "With headers: " + headers);
//
//        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url, headers);
//
//        compositeDisposable.add(stompClient.lifecycle().subscribe(lifecycleEvent -> {
//            switch (lifecycleEvent.getType()) {
//                case OPENED:
//                    Log.d(TAG, "🔓 Connected to WebSocket. Subscribing to notifications...");
//                    subscribeToNotifications();
//                    break;
//                case ERROR:
//                    Log.e(TAG, "⚠️ STOMP error: " + lifecycleEvent.getException().getMessage());
//                    break;
//                case CLOSED:
//                    Log.w(TAG, "🔒 WebSocket connection closed.");
//                    break;
//            }
//        }));
//
//        stompClient.connect();
//    }
//
//    /**
//     * Diskonektovanje
//     */
//    public void disconnect() {
//        if (stompClient != null) {
//            stompClient.disconnect();
//            compositeDisposable.clear();
//            Log.d(TAG, "Disconnected from WebSocket.");
//        }
//    }
//}
//













//drugi pokusaj

//import android.util.Log;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import io.reactivex.disposables.CompositeDisposable;
//import io.reactivex.disposables.Disposable;
//import io.reactivex.functions.Consumer;
//import ua.naiksoftware.stomp.Stomp;
//import ua.naiksoftware.stomp.StompClient;
//import ua.naiksoftware.stomp.dto.StompHeader;
//import ua.naiksoftware.stomp.dto.StompMessage;
//import ua.naiksoftware.stomp.dto.LifecycleEvent;
//
//public class NotificationWebSocketService {
//
//    private static final String TAG = "WebSocketService";
//    private StompClient stompClient;
//
//    // NOVI KOD: Kompozitni 'disposable' za upravljanje vezom
//    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
//    private List<NotificationCountListener> listeners = new ArrayList<>();
//    private int unreadCount = 1;
//
//    public interface NotificationCountListener {
//        void onUnreadCountChanged(int newCount);
//    }
//
//    public void addListener(NotificationCountListener listener) {
//        if (!listeners.contains(listener)) {
//            listeners.add(listener);
//        }
//    }
//
//    public void removeListener(NotificationCountListener listener) {
//        listeners.remove(listener);
//    }
//
//    private void notifyListeners() {
//        for (NotificationCountListener listener : listeners) {
//            listener.onUnreadCountChanged(unreadCount);
//        }
//    }
//
//    public int getUnreadCount() {
//        return unreadCount;
//    }
//
//    public void markAllAsRead() {
//        unreadCount = 0;
//        notifyListeners();
//    }
//
//    // NOVI KOD: Metoda za pretplatu na notifikacije
//    private void subscribeToNotifications() {
//        if (stompClient.isConnected()) {
//
//            Disposable disposable = stompClient.topic("/user/queue/notifications")
//                    .subscribe(stompMessage -> {
//                        Log.d(TAG, "Received message: " + stompMessage.getPayload());
//                        // Povećajte brojač nepročitanih poruka
//                        unreadCount++;
//                        notifyListeners();
//                    }, new Consumer<Throwable>() {
//                        @Override
//                        public void accept(Throwable throwable) throws Exception {
//                            Log.e(TAG, "Error on topic subscription", throwable);
//                        }
//                    });
//            compositeDisposable.add(disposable);
//        }
//    }
//
//    public void connect(String jwtToken) {
//        if (stompClient != null && stompClient.isConnected()) {
//            Log.d(TAG, "WebSocket is already connected.");
//            return;
//        }
//
//        if (jwtToken == null || jwtToken.isEmpty()) {
//            Log.e(TAG, "JWT token is missing or empty. Cannot connect to WebSocket.");
//            return;
//        }
//
//        // Koristimo ispravnu URL adresu koju ste potvrdili
//        String url = "ws://192.168.100.26:8080/ws/websocket";
//
//        // NOVO: Kreirajte Mapu sa hederima
//        Map<String, String> headers = new HashMap<>();
//        headers.put("Authorization",jwtToken);
//
//        // Proveravamo da li je URL ispravan
//        Log.d(TAG, "Attempting to connect to: " + url);
//        Log.d(TAG, "With headers: " + headers.toString());
//
//        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url, headers);
//
//        compositeDisposable.add(stompClient.lifecycle().subscribe(lifecycleEvent -> {
//            switch (lifecycleEvent.getType()) {
//                case OPENED:
//                    Log.d(TAG, "Connected to WebSocket.");
//                    subscribeToNotifications();
//                    break;
//                case ERROR:
//                    Log.e(TAG, "STOMP error: " + lifecycleEvent.getException().getMessage());
//                    break;
//                case CLOSED:
//                    Log.w(TAG, "WebSocket connection closed.");
//                    break;
//            }
//        }));
//
//        stompClient.connect();
//    }







//    public void connect(String jwtToken) {
//        if (stompClient != null && stompClient.isConnected()) {
//            Log.d(TAG, "WebSocket is already connected.");
//            return;
//        }
//
//        if (jwtToken == null || jwtToken.isEmpty()) {
//            Log.e(TAG, "JWT token is missing or empty. Cannot connect to WebSocket.");
//            return;
//        }
//
//        String url = "ws://192.168.100.26:8080/ws/websocket";
//
//        Map<String, String> headersMap = new HashMap<>();
//        headersMap.put("Authorization", jwtToken);
//
//        // NOVI KOD: Kreiranje Stomp klijenta sa headerima
//        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url, headersMap);
//
//        // NOVI KOD: Povezivanje na životni ciklus
//        compositeDisposable.add(stompClient.lifecycle().subscribe(new Consumer<LifecycleEvent>() {
//            @Override
//            public void accept(LifecycleEvent lifecycleEvent) throws Exception {
//                switch (lifecycleEvent.getType()) {
//                    case OPENED:
//                        Log.d(TAG, "Connected to WebSocket.");
//                        // Pretplata na notifikacije nakon uspostavljene veze
//                        subscribeToNotifications();
//                        break;
//                    case ERROR:
//                        Log.e(TAG, "STOMP error: " + lifecycleEvent.getException().getMessage());
//                        break;
//                    case CLOSED:
//                        Log.w(TAG, "WebSocket connection closed.");
//                        break;
//                }
//            }
//        }));
//
//        List<StompHeader> headersList = new ArrayList<>();
//        headersList.add(new StompHeader("Authorization", jwtToken));
//        stompClient.connect(headersList);
//    }

//    public void disconnect() {
//        if (stompClient != null) {
//            stompClient.disconnect();
//            compositeDisposable.dispose();
//            Log.d(TAG, "Disconnected from WebSocket.");
//        }
//    }
//}





// prvi pokusaj
//import android.util.Log;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import java.lang.reflect.Type;
//import java.util.ArrayList;
//import java.util.List;
//import ua.naiksoftware.stomp.Stomp;
//import ua.naiksoftware.stomp.StompClient;
//import ua.naiksoftware.stomp.dto.StompHeader;
////import ua.naiksoftware.stomp.dto.StompMessage;
////import ua.naiksoftware.stomp.lifecycle.LifecycleEvent;
//
//public class NotificationWebSocketService {
//
//    private static final String TAG = "WebSocketService";
//    private StompClient stompClient;
//    private int unreadCount = 0;
//    private boolean isViewingNotifications = false;
//
//    // Listener interfejs za obaveštavanje o promenama broja
//    public interface NotificationCountListener {
//        void onUnreadCountChanged(int newCount);
//    }
//
//    private final List<NotificationCountListener> listeners = new ArrayList<>();
//
//    public void addListener(NotificationCountListener listener) {
//        listeners.add(listener);
//    }
//
//    public void removeListener(NotificationCountListener listener) {
//        listeners.remove(listener);
//    }
//
//    private void notifyListeners() {
//        for (NotificationCountListener listener : listeners) {
//            listener.onUnreadCountChanged(unreadCount);
//        }
//    }
//
//    public void setViewingNotifications(boolean isViewing) {
//        this.isViewingNotifications = isViewing;
//        if (isViewing) {
//            markAllAsRead();
//        }
//    }
//
//    public int getUnreadCount() {
//        return unreadCount;
//    }
//
//    public void connect(String jwtToken) {
//        if (stompClient != null && stompClient.isConnected()) {
//            Log.d(TAG, "WebSocket is already connected.");
//            return;
//        }
//
//        if (jwtToken == null || jwtToken.isEmpty()) {
//            Log.e(TAG, "JWT token is missing or empty. Cannot connect to WebSocket.");
//            return;
//        }else{
//            Log.e(TAG,"token je tu "+ jwtToken);
//        }
//
//        String url = "ws://192.168.100.26:8080/ws";
//        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url);
//
//        List<StompHeader> headers = new ArrayList<>();
//        headers.add(new StompHeader("Authorization", jwtToken));
//
//        stompClient.connect(headers);
//
//        stompClient.lifecycle().subscribe(lifecycleEvent -> {
//            switch (lifecycleEvent.getType()) {
//                case OPENED:
//                    Log.d(TAG, "Connected to WebSocket.");
//                    subscribeToNotifications();
//                    break;
//                case ERROR:
//                    Log.e(TAG, "STOMP error: " + lifecycleEvent.getException().getMessage());
//                    break;
//                case CLOSED:
//                    Log.w(TAG, "WebSocket connection closed.");
//                    break;
//            }
//        });
//    }
//
//    private void subscribeToNotifications() {
//        stompClient.topic("/user/queue/notifications").subscribe(stompMessage -> {
//            Log.d(TAG, "New notification received: " + stompMessage.getPayload());
//
//            // Ovde parsirajte notifikaciju, ako je potrebno
//            // NotificationModel newNotification = parseNotification(stompMessage.getPayload());
//
//            if (!isViewingNotifications) {
//                unreadCount++;
//                notifyListeners();
//            }
//        }, throwable -> {
//            Log.e(TAG, "Error on subscription", throwable);
//        });
//    }
//
//    public void markAllAsRead() {
//        if (unreadCount > 0) {
//            unreadCount = 0;
//            notifyListeners();
//        }
//    }
//
//    public void disconnect() {
//        if (stompClient != null && stompClient.isConnected()) {
//            stompClient.disconnect();
//            Log.d(TAG, "Disconnected from WebSocket.");
//        }
//    }
//}
