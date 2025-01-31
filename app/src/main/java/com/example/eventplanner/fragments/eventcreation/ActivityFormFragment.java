package com.example.eventplanner.fragments.eventcreation;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.eventplanner.R;
import com.example.eventplanner.ValidationUtils;
import com.example.eventplanner.adapters.AgendaAdapter;
import com.example.eventplanner.dto.agenda.CreateActivityDTO;
import com.example.eventplanner.model.Activity;
import com.example.eventplanner.viewmodels.EventCreationViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

            // validate input data
            if (!ValidationUtils.isFieldValid(timeField, "Time is required!")) return;
            if (!ValidationUtils.isActivityTimeValid(timeField)) return;
            if (!ValidationUtils.isFieldValid(nameField, "Name is required!")) return;
            if (!ValidationUtils.isFieldValid(descriptionField, "Description is required!")) return;
            if (!ValidationUtils.isFieldValid(venueField, "Venue is required!")) return;


            // if valid, save
            String time = timeField.getText().toString();
            String description = descriptionField.getText().toString();
            String venue = venueField.getText().toString();
            String name = nameField.getText().toString();

            CreateActivityDTO newActivity = new CreateActivityDTO(time, name, description, venue);

            viewModel.updateAgenda(newActivity);

            dismiss();


        });

        return view;
    }


    public void closeForm(View view) {
        dismiss();
    }

}