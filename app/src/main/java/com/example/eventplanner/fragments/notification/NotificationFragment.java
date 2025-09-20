package com.example.eventplanner.fragments.notification;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.notification.NotificationAdapter;
import com.example.eventplanner.dto.notification.GetNotificationDTO;
import com.example.eventplanner.utils.ClientUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<GetNotificationDTO> notificationList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        recyclerView = view.findViewById(R.id.notifications_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NotificationAdapter(getContext(), notificationList);
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.mute_button).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Mute clicked!", Toast.LENGTH_SHORT).show();
        });

        loadNotifications();

        return view;
    }

    private void loadNotifications() {
        String auth = ClientUtils.getAuthorization(getContext());
        Call<List<GetNotificationDTO>> call = ClientUtils.notificationService.getFilteredNotifications(auth);

        call.enqueue(new Callback<List<GetNotificationDTO>>() {
            @Override
            public void onResponse(Call<List<GetNotificationDTO>> call, Response<List<GetNotificationDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    notificationList.clear();
                    notificationList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Error loading notifications: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e("NotificationFragment", "Error loading notifications: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<GetNotificationDTO>> call, Throwable t) {
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("NotificationFragment", "Network error occurred", t);
            }
        });
    }
}