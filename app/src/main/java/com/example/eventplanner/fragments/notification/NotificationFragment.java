package com.example.eventplanner.fragments.notification;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
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

    private static final int PAGE_SIZE = 5;

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<GetNotificationDTO> allNotifications = new ArrayList<>();
    private List<GetNotificationDTO> currentNotifications = new ArrayList<>();

    private ImageButton prevPageButton;
    private ImageButton nextPageButton;

    private TextView pageIndicatorTextView;
    private int currentPage = 0;
    private int totalPages = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        recyclerView = view.findViewById(R.id.notifications_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NotificationAdapter(getContext(), currentNotifications);
        recyclerView.setAdapter(adapter);

        prevPageButton = view.findViewById(R.id.prevPageButton);
        nextPageButton = view.findViewById(R.id.nextPageButton);
        pageIndicatorTextView = new TextView(getContext());

        ViewGroup paginationIndicators = view.findViewById(R.id.paginationIndicators);
        paginationIndicators.addView(pageIndicatorTextView);

        prevPageButton.setOnClickListener(v -> showPreviousPage());
        nextPageButton.setOnClickListener(v -> showNextPage());

        prevPageButton.setEnabled(false);
        nextPageButton.setEnabled(false);

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
                    allNotifications.clear();
                    allNotifications.addAll(response.body());
                    setupPagination();
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

    private void setupPagination() {
        if (allNotifications.isEmpty()) {
            Toast.makeText(getContext(), "No notifications to show.", Toast.LENGTH_SHORT).show();
            pageIndicatorTextView.setText("");
            return;
        }

        totalPages = (int) Math.ceil((double) allNotifications.size() / PAGE_SIZE);
        currentPage = 0;
        displayCurrentPage();
    }

    private void displayCurrentPage() {
        int startIndex = currentPage * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, allNotifications.size());

        currentNotifications.clear();
        if (startIndex < endIndex) {
            currentNotifications.addAll(allNotifications.subList(startIndex, endIndex));
        }

        adapter.notifyDataSetChanged();
        updateButtonStates();
        updatePageIndicator();
    }

    private void showNextPage() {
        if (currentPage < totalPages - 1) {
            currentPage++;
            displayCurrentPage();
        }
    }

    private void showPreviousPage() {
        if (currentPage > 0) {
            currentPage--;
            displayCurrentPage();
        }
    }

    private void updateButtonStates() {
        prevPageButton.setEnabled(currentPage > 0);
        nextPageButton.setEnabled(currentPage < totalPages - 1);
    }

    private void updatePageIndicator() {
        String pageText = (currentPage + 1) + "/" + totalPages;
        pageIndicatorTextView.setText(pageText);
    }
}