package com.example.eventplanner.fragments.homepage;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.activities.homepage.HomepageService;
import com.example.eventplanner.adapters.homepage.CardAdapter;
import com.example.eventplanner.dto.event.GetEventDTO;
import com.example.eventplanner.utils.ClientUtils;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TopEventsFragment extends Fragment {

    private RecyclerView eventsRv;
    private CardAdapter adapter;
    private HomepageService service;
    private View emptyView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_homepage_cards, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventsRv = view.findViewById(R.id.eventsRecyclerView);

        eventsRv.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        adapter = new CardAdapter(requireContext());
        eventsRv.setAdapter(adapter);
        emptyView = view.findViewById(R.id.empty_view);

        service = ClientUtils.retrofit.create(HomepageService.class);

        loadTopEvents();
    }


    @Override
    public void onResume() {
        super.onResume();
        adapter.setItems(Collections.emptyList());
        loadTopEvents();
    }

    private void loadTopEvents() {
        adapter.setItems(Collections.emptyList());
        SharedPreferences sp = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String token = sp.getString("token", null);

        Call<List<GetEventDTO>> call;
        if (token != null && !token.isEmpty()) {
            call = service.getTop5Events("Bearer " + token);
        } else {
            call = service.getTop5Events(null);
        }

        call.enqueue(new Callback<List<GetEventDTO>>() {
            @Override
            public void onResponse(Call<List<GetEventDTO>> call, Response<List<GetEventDTO>> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    adapter.setItems(response.body());
                    eventsRv.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                } else {
                    adapter.setItems(Collections.emptyList());
                    eventsRv.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<GetEventDTO>> call, Throwable t) {
                if (!isAdded()) return;
                adapter.setItems(Collections.emptyList());
                eventsRv.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            }
        });
    }


}
