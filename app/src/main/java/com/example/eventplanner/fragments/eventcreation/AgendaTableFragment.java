package com.example.eventplanner.fragments.eventcreation;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.AgendaAdapter;
import com.example.eventplanner.model.Activity;

import java.util.ArrayList;
import java.util.List;


public class AgendaTableFragment extends Fragment {

    private RecyclerView agendaRecyclerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_agenda_table, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        agendaRecyclerView = view.findViewById(R.id.agendaRecyclerView);
        agendaRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Activity> activities = new ArrayList<>();
        activities.add(new Activity("[ 10:00 - 12:00 ]", "A1", "desc1", "location1"));
        activities.add(new Activity("[ 13:00 - 14:00 ]", "A2", "desc2", "location2"));
        activities.add(new Activity("[ 15:00 - 16:00 ]", "A3", "desc3", "location3"));
        activities.add(new Activity("[ 17:00 - 18:00 ]", "A4", "desc4", "location4"));

        AgendaAdapter adapter = new AgendaAdapter(activities);
        agendaRecyclerView.setAdapter(adapter);

    }


}