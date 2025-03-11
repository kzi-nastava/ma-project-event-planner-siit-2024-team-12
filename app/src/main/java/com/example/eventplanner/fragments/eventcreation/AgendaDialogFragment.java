package com.example.eventplanner.fragments.eventcreation;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.AgendaAdapter;
import com.example.eventplanner.model.Activity;

import java.util.List;


public class AgendaDialogFragment extends DialogFragment {

    private List<Activity> activities;

    public AgendaDialogFragment(List<Activity> activities) {
        this.activities = activities;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_agenda_dialog, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewAgenda);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        AgendaAdapter agendaAdapter = new AgendaAdapter(activities, false);
        recyclerView.setAdapter(agendaAdapter);
        return view;
    }
}