package com.example.eventplanner.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.ClientUtils;
import com.example.eventplanner.R;
import com.example.eventplanner.adapters.EventTypeAdapter;
import com.example.eventplanner.model.EventType;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventTypeTableFragment extends Fragment {

    private RecyclerView eventTypeRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate fragment layout
        return inflater.inflate(R.layout.fragment_event_type, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize RecyclerView
        eventTypeRecyclerView = view.findViewById(R.id.eventTypeRecyclerView);
        eventTypeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Prepare data
        final List<EventType>[] eventTypes = new List[]{new ArrayList<>()};

        Call<ArrayList<EventType>> call = ClientUtils.eventTypeService.getAll();
        call.enqueue(new Callback<ArrayList<EventType>>() {
            @Override
            public void onResponse(Call<ArrayList<EventType>> call, Response<ArrayList<EventType>> response) {
                if (response.code() == 200) {
                    System.out.println("DOBAVLJENO " + response.body());
                    eventTypes[0] = response.body();

                    // Set adapter
                    EventTypeAdapter adapter = new EventTypeAdapter(eventTypes[0]);
                    eventTypeRecyclerView.setAdapter(adapter);
                }
                else {
                    Log.d("REZ", "NOPE");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<EventType>> call, Throwable t) {
                Log.e("API_ERROR", "Failed to connect", t);


            }
        });




    }
}
