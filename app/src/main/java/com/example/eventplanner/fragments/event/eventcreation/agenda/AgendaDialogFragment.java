package com.example.eventplanner.fragments.event.eventcreation.agenda;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.event.AgendaAdapter;
import com.example.eventplanner.model.Activity;

import java.util.List;
import java.util.Objects;


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


    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            Objects.requireNonNull(getDialog().getWindow()).setLayout(1000, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

}