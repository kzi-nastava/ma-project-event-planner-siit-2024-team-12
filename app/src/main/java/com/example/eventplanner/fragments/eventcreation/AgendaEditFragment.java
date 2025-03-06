package com.example.eventplanner.fragments.eventcreation;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.eventplanner.R;
import com.example.eventplanner.dto.agenda.CreateActivityDTO;
import com.example.eventplanner.dto.event.EventDetailsDTO;
import com.example.eventplanner.model.Activity;
import com.example.eventplanner.viewmodels.EventEditViewModel;

import java.util.ArrayList;


public class AgendaEditFragment extends DialogFragment {

    private ArrayList<Activity> activities;
    View view;
    private EventDetailsDTO detailsDTO;
    private EventEditViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_agenda_edit, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(EventEditViewModel.class);


        // retrieve event details from Bundle
        if (getArguments() != null) {
            detailsDTO = (EventDetailsDTO) getArguments().getSerializable("passed_details");
        }

        // pass event details to agenda table
        AgendaTableFragment agendaTableFragment = new AgendaTableFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("passed_details", detailsDTO);
        agendaTableFragment.setArguments(bundle);

        getChildFragmentManager().beginTransaction()
                .replace(R.id.eventTypeFragmentContainer, agendaTableFragment)
                .commit();

        setUpActivityBtn();

        setUpSaveBtn();

        return view;
    }


    private void setUpSaveBtn() {
        Button saveBtn = view.findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(v -> {
            dismiss();
        });
    }


    private void setUpActivityBtn() {
        Button activityBtn = view.findViewById(R.id.activityBtn);

        activityBtn.setOnClickListener(v -> {
            ActivityFormFragment activityForm = ActivityFormFragment.newInstance(true);
            activityForm.show(getChildFragmentManager(), "ActivityForm");
        });
    }


    public static AgendaEditFragment newInstance(EventDetailsDTO detailsDTO) {
        AgendaEditFragment fragment = new AgendaEditFragment();
        Bundle args = new Bundle();
        args.putSerializable("passed_details", detailsDTO);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.setCanceledOnTouchOutside(true);
        }
    }

}
