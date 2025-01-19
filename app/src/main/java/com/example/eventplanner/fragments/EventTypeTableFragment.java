package com.example.eventplanner.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.EventTypeAdapter;
import com.example.eventplanner.model.EventType;

import java.util.ArrayList;
import java.util.List;

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
        List<EventType> events = new ArrayList<>();
        events.add(new EventType("1", "Concert", "Active"));
        events.add(new EventType("2", "Meeting", "Inactive"));
        events.add(new EventType("3", "Meeting", "Inactive"));
        events.add(new EventType("4", "Meeting", "Inactive"));
        events.add(new EventType("5", "Meeting", "Inactive"));
        events.add(new EventType("6", "Meeting", "Inactive"));
        events.add(new EventType("7", "Meeting", "Inactive"));
        events.add(new EventType("8", "Meeting", "Inactive"));
        events.add(new EventType("9", "Meeting", "Inactive"));
        events.add(new EventType("10", "Meeting", "Inactive"));

        // Set adapter
        EventTypeAdapter adapter = new EventTypeAdapter(events);
        eventTypeRecyclerView.setAdapter(adapter);
    }
}
