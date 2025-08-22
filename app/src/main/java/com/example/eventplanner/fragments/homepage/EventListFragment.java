package com.example.eventplanner.fragments.homepage;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
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
import com.example.eventplanner.activities.homepage.HomepageService;
import com.example.eventplanner.adapters.event.EventListAdapter;
import com.example.eventplanner.dto.event.GetEventDTO;
import com.example.eventplanner.utils.ClientUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ImageButton prevPageButton, nextPageButton;
    private LinearLayout paginationIndicators;

    private EventListAdapter adapter;
    private HomepageService service;

    private final List<GetEventDTO> allEvents = new ArrayList<>();
    private int currentPage = 0;
    private final int pageSize = 2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);

        recyclerView = view.findViewById(R.id.eventRecyclerView);
        prevPageButton = view.findViewById(R.id.prevPageButton);
        nextPageButton = view.findViewById(R.id.nextPageButton);
        paginationIndicators = view.findViewById(R.id.paginationIndicators);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EventListAdapter(requireContext());
        recyclerView.setAdapter(adapter);

        service = ClientUtils.retrofit.create(HomepageService.class);

        loadAllEventsFromBackend();

        setupPaginationButtons();

        return view;
    }

    private void loadAllEventsFromBackend() {
        SharedPreferences sp = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String token = sp.getString("token", null);

        Call<List<GetEventDTO>> call = service.searchEvents(
                null, null, null, null, null, null, null,
                null, null, null, null,
                "date", "asc", 0, 1000, false, true
        );

        if (token != null && !token.isEmpty()) {
            call = service.searchEvents(
                    null, null, null, null, null, null, null,
                    null, null, null, null,
                    "date", "asc", 0, 1000, false, true
            );
        }

        call.enqueue(new Callback<List<GetEventDTO>>() {
            @Override
            public void onResponse(Call<List<GetEventDTO>> call, Response<List<GetEventDTO>> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    allEvents.clear();
                    allEvents.addAll(response.body());
                    currentPage = 0;
                    updateRecyclerView();
                } else {
                    Toast.makeText(requireContext(), "No events to show.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<GetEventDTO>> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupPaginationButtons() {
        prevPageButton.setOnClickListener(v -> {
            if (currentPage > 0) {
                currentPage--;
                updateRecyclerView();
            }
        });

        nextPageButton.setOnClickListener(v -> {
            if ((currentPage + 1) * pageSize < allEvents.size()) {
                currentPage++;
                updateRecyclerView();
            }
        });
    }

    private void updateRecyclerView() {
        int totalPages = (int) Math.ceil((double) allEvents.size() / pageSize);
        int start = currentPage * pageSize;
        int end = Math.min(start + pageSize, allEvents.size());
        adapter.updateData(allEvents.subList(start, end));
        updatePaginationIndicators(totalPages);
    }

    private void updatePaginationIndicators(int totalPages) {
        if (getView() == null) return;
        paginationIndicators.removeAllViews();

        TextView pageInfo = new TextView(getContext());
        pageInfo.setText((currentPage + 1) + " / " + totalPages);
        pageInfo.setTextSize(14);
        pageInfo.setTextColor(Color.DKGRAY);
        pageInfo.setGravity(Gravity.CENTER);

        paginationIndicators.addView(pageInfo);
    }


    private void addCircle(int page, boolean isCurrent) {
        TextView circle = new TextView(getContext());
        circle.setText(String.valueOf(page + 1));
        circle.setTextSize(12);
        circle.setTextColor(isCurrent ? Color.WHITE : Color.GRAY);
        circle.setBackgroundResource(R.drawable.circle_indicator);
        circle.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(8, 0, 8, 0);
        circle.setLayoutParams(params);

        paginationIndicators.addView(circle);
    }

    private void addEllipsis() {
        TextView ellipsis = new TextView(getContext());
        ellipsis.setText("...");
        ellipsis.setTextSize(12);
        ellipsis.setTextColor(Color.GRAY);
        ellipsis.setGravity(Gravity.CENTER);

        paginationIndicators.addView(ellipsis);
    }
}

