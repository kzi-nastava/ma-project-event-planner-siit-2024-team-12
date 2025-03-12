package com.example.eventplanner.fragments.eventcreation;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.AgendaAdapter;
import com.example.eventplanner.dto.agenda.CreateActivityDTO;
import com.example.eventplanner.dto.event.EventDetailsDTO;
import com.example.eventplanner.model.Activity;
import com.example.eventplanner.viewmodels.EventCreationViewModel;
import com.example.eventplanner.viewmodels.EventEditViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


public class AgendaTableFragment extends Fragment {
    private View view;
    private EventCreationViewModel viewModel;
    private RecyclerView agendaRecyclerView;
    private List<Activity> tableDisplay;
    private EventEditViewModel editViewModel;
    private Boolean isEditable = false;


    public static AgendaTableFragment newInstance(Boolean isEditable) {
        AgendaTableFragment fragment = new AgendaTableFragment();
        Bundle args = new Bundle();
        args.putSerializable("is_editable", isEditable);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_agenda_table, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(EventCreationViewModel.class);
        editViewModel = new ViewModelProvider(requireActivity()).get(EventEditViewModel.class);


        if (getArguments() != null) {
            isEditable = (Boolean) getArguments().getSerializable("is_editable");
        }


        return view;
    }


    private List<Activity> convertDTOToActivity(List<CreateActivityDTO> activityDTOS) {
        List<Activity> activities = new ArrayList<>();
        for (CreateActivityDTO dto : activityDTOS) {
            activities.add(new Activity(dto.getTime(), dto.getName(), dto.getDescription(), dto.getLocation()));
        }
        return activities;
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        agendaRecyclerView = view.findViewById(R.id.agendaRecyclerView);
        agendaRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        if (getArguments() != null) {
            EventDetailsDTO dto = (EventDetailsDTO) getArguments().getSerializable("passed_details");
            assert dto != null;
            tableDisplay = convertDTOToActivity(dto.getActivities());

        } else {
            tableDisplay = new ArrayList<>();
        }


        AgendaAdapter adapter = new AgendaAdapter(tableDisplay, isEditable);
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


        // track changes in editViewModel to dynamically display them in agenda table
        editViewModel.getDto().observe(getViewLifecycleOwner(), editEventDTO -> {
            if (editEventDTO != null && editEventDTO.getActivities() != null) {
                tableDisplay.clear();


                // using LinkedHashSet to keep the order from getActivities()
                // order is important because activity edit depends on the position in the list
                Set<CreateActivityDTO> uniques = new LinkedHashSet<>(editEventDTO.getActivities());

                for (CreateActivityDTO activityDTO : uniques) {
                    Activity activity = new Activity(activityDTO.getTime(), activityDTO.getName(),
                            activityDTO.getDescription(), activityDTO.getLocation());
                    tableDisplay.add(activity);
                }

                adapter.notifyDataSetChanged();
            }
        });
    }
}