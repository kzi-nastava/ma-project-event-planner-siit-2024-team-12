package com.example.eventplanner.fragments.notification;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.activities.event.EventDetailsActivity;
import com.example.eventplanner.activities.homepage.HomepageActivity;
import com.example.eventplanner.activities.product.ProductDetailsActivity;
import com.example.eventplanner.adapters.notification.NotificationAdapter;
import com.example.eventplanner.dto.notification.GetNotificationDTO;
import com.example.eventplanner.utils.ClientUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    private ImageButton muteToggleButton;
    private LinearLayout muteOptionsContainer;
    private TextView muteStatusText;
    private TextView muteOptionsTitle;

    private TextView muteEndDateText;

    private Button mute15minButton, mute1hButton, mute4hButton, muteIndefinitelyButton;

    private int currentPage = 0;
    private int totalPages = 0;
    private boolean isMuted = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        recyclerView = view.findViewById(R.id.notifications_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NotificationAdapter(getContext(), currentNotifications, notification -> {
            Intent intent;
            switch (notification.getEntityType()) {
                case "event":
                    intent = new Intent(getContext(), EventDetailsActivity.class);
                    intent.putExtra("id", notification.getEntityId());
                    startActivity(intent);
                    break;
                case "product":
                    intent = new Intent(getContext(), ProductDetailsActivity.class);
                    intent.putExtra("id", notification.getEntityId());
                    startActivity(intent);
                    break;
                case "SERVICE_RESERVATION":
                    Toast.makeText(getContext(), "Service reservation details not implemented.", Toast.LENGTH_SHORT).show();
                    break;
                case "SERVICE":
                    Toast.makeText(getContext(), "Service details not implemented.", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(getContext(), "Unknown notification type.", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
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

        muteToggleButton = view.findViewById(R.id.mute_toggle_button);
        muteStatusText = view.findViewById(R.id.mute_status_text);
        muteEndDateText = view.findViewById(R.id.mute_end_date_text);
        muteOptionsContainer = view.findViewById(R.id.mute_options_container);
        muteOptionsTitle = view.findViewById(R.id.mute_options_title);
        mute15minButton = view.findViewById(R.id.mute_15min_button);
        mute1hButton = view.findViewById(R.id.mute_1h_button);
        mute4hButton = view.findViewById(R.id.mute_4h_button);
        muteIndefinitelyButton = view.findViewById(R.id.mute_indefinitely_button);

        muteToggleButton.setOnClickListener(v -> toggleMuteOptions());

        mute15minButton.setOnClickListener(v -> muteNotifications(15L));
        mute1hButton.setOnClickListener(v -> muteNotifications(60L));
        mute4hButton.setOnClickListener(v -> muteNotifications(240L));
        muteIndefinitelyButton.setOnClickListener(v -> muteNotifications(null));

        checkMuteStatus();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() instanceof HomepageActivity) {
            NotificationWebSocketService service =
                    ((HomepageActivity) getActivity()).getNotificationService();

            if (service != null) {
                service.addNotificationListener(this::handleNewNotification);
            }
        }

        loadNotifications();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof HomepageActivity) {
            ((HomepageActivity) getActivity()).onNotificationsOpened();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() instanceof HomepageActivity) {
            ((HomepageActivity) getActivity()).onNotificationsClosed();
        }
    }


    private void handleNewNotification(GetNotificationDTO notification) {
        if (getActivity() == null) return;

        allNotifications.add(0, notification);

        if (currentPage == 0) {
            currentNotifications.add(0, notification);
            if (currentNotifications.size() > PAGE_SIZE) {
                currentNotifications.remove(currentNotifications.size() - 1);
            }
            requireActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
        }

        totalPages = (int) Math.ceil((double) allNotifications.size() / PAGE_SIZE);
        updatePageIndicator();
        updateButtonStates();
    }

    private void toggleMuteOptions() {
        if (isMuted) {
            unmuteNotifications();
        } else {
            if (muteOptionsContainer.getVisibility() == View.VISIBLE) {
                muteOptionsContainer.setVisibility(View.GONE);
                muteStatusText.setText(R.string.mute_notifications);
            } else {
                muteOptionsContainer.setVisibility(View.VISIBLE);
                //muteStatusText.setText(R.string.mute_for);
            }
        }
    }

    private void muteNotifications(Long minutes) {
        String auth = ClientUtils.getAuthorization(getContext());
        Call<Void> call = ClientUtils.notificationService.muteNotifications(auth, minutes);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    checkMuteStatus();
                    Toast.makeText(getContext(), "Notifications muted successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to mute notifications: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void unmuteNotifications() {
        String auth = ClientUtils.getAuthorization(getContext());
        Call<Void> call = ClientUtils.notificationService.unmuteNotifications(auth);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    isMuted = false;
                    updateMuteUI(null);
                    Toast.makeText(getContext(), "Notifications unmuted successfully.", Toast.LENGTH_SHORT).show();
                    loadNotifications();
                } else {
                    Toast.makeText(getContext(), "Failed to unmute notifications: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkMuteStatus() {
        String auth = ClientUtils.getAuthorization(getContext());
        Call<String> call = ClientUtils.notificationService.getMuteStatus(auth);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String mutedUntilString = response.body();
                    if (mutedUntilString != null && !mutedUntilString.isEmpty()) {
                        try {
                            LocalDateTime muteEndTime = LocalDateTime.parse(mutedUntilString);
                            isMuted = muteEndTime.isAfter(LocalDateTime.now());
                            updateMuteUI(isMuted ? muteEndTime : null);
                        } catch (Exception e) {
                            Log.e("NotificationFragment", "Error parsing muteUntil date: " + mutedUntilString, e);
                            isMuted = false;
                            updateMuteUI(null);
                        }
                    } else {
                        isMuted = false;
                        updateMuteUI(null);
                    }
                } else {
                    isMuted = false;
                    updateMuteUI(null);
                }
                loadNotifications();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                isMuted = false;
                updateMuteUI(null);
                Log.e("NotificationFragment", "Network error checking mute status", t);
                loadNotifications();
            }
        });
    }
    private void updateMuteUI(LocalDateTime mutedUntil) {
        if (isMuted) {
            muteToggleButton.setImageResource(R.drawable.ic_mute_notifications);
            muteStatusText.setText(R.string.unmute_notifications);
            muteEndDateText.setVisibility(View.VISIBLE);
            muteOptionsContainer.setVisibility(View.GONE);

            if (mutedUntil.getYear() == 3000) {
                muteEndDateText.setText(R.string.unmute_indefinitely);
            } else {
                muteEndDateText.setText(getString(R.string.notifications_muted_until,
                        mutedUntil.format(DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy"))));
            }

        } else {
            muteToggleButton.setImageResource(R.drawable.ic_notifications);
            muteStatusText.setText(R.string.mute_notifications_status);
            muteEndDateText.setVisibility(View.GONE);
            muteOptionsContainer.setVisibility(View.GONE);
        }
    }

    private void loadNotifications() {
        String auth = ClientUtils.getAuthorization(getContext());

        if (isMuted) {
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
        } else {
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