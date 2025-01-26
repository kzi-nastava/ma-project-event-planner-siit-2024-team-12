package com.example.eventplanner.fragments.eventcreation;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.eventplanner.R;
import com.example.eventplanner.dto.agenda.CreateActivityDTO;
import com.example.eventplanner.viewmodels.EventCreationViewModel;

import java.util.ArrayList;
import java.util.List;


public class ActivityFormFragment extends DialogFragment {
    View view;
    EventCreationViewModel viewModel;

    public ActivityFormFragment() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_activity_form, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(EventCreationViewModel.class);

        Button addBtn = view.findViewById(R.id.addBtn);
        addBtn.setOnClickListener(v -> {
            EditText timeField = view.findViewById(R.id.time);
            EditText descriptionField = view.findViewById(R.id.description);
            EditText venueField = view.findViewById(R.id.venue);
            EditText nameField = view.findViewById(R.id.name);


            String time = timeField.getText().toString();
            String description = descriptionField.getText().toString();
            String venue = venueField.getText().toString();
            String name = nameField.getText().toString();

            CreateActivityDTO newActivity = new CreateActivityDTO(time, name, description, venue);

            List<CreateActivityDTO> singleActivityList = new ArrayList<>();
            singleActivityList.add(newActivity);
            viewModel.updateAgenda(singleActivityList);

            dismiss();


        });

        return view;
    }


    public void closeForm(View view) {
        dismiss();
    }



}