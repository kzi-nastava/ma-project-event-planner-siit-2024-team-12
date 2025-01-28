package com.example.eventplanner.fragments.eventcreation;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.AgendaAdapter;
import com.example.eventplanner.dto.agenda.CreateActivityDTO;
import com.example.eventplanner.model.Activity;
import com.example.eventplanner.viewmodels.EventCreationViewModel;

import java.util.ArrayList;
import java.util.List;


public class AgendaTableFragment extends Fragment {
    View view;
    EventCreationViewModel viewModel;
    private RecyclerView agendaRecyclerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_agenda_table, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(EventCreationViewModel.class);

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        agendaRecyclerView = view.findViewById(R.id.agendaRecyclerView);
        agendaRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Activity> tableDisplay = new ArrayList<>();
        AgendaAdapter adapter = new AgendaAdapter(tableDisplay);
        agendaRecyclerView.setAdapter(adapter);

        // track changes in viewModel to dynamically display them in agenda table
        viewModel.getDto().observe(getViewLifecycleOwner(), createEventDTO -> {
            if (createEventDTO != null && createEventDTO.getAgenda() != null) {
                tableDisplay.clear();

                for (CreateActivityDTO activityDTO : createEventDTO.getAgenda()) {
                    Activity activity = new Activity(activityDTO.getTime(), activityDTO.getName(),
                            activityDTO.getDescription(), activityDTO.getLocation());
                    tableDisplay.add(activity);
                }

                adapter.notifyDataSetChanged();
            }
        });
    }

}