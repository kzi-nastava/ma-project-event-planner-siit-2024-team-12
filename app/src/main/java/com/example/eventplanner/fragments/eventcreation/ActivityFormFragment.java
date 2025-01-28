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
            if (!validateField(timeField, "Time is required!")) return;
            if (!validateTimeFormat(timeField)) return;
            if (!validateField(nameField, "Name is required!")) return;
            if (!validateField(descriptionField, "Description is required!")) return;
            if (!validateField(venueField, "Venue is required!")) return;


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


    private boolean validateField(EditText field, String errorMessage) {
        String value = field.getText().toString().trim();
        if (value.isEmpty()) {
            field.setError(errorMessage);
            field.requestFocus();
            return false;
        }
        return true;
    }


    private boolean validateTimeFormat(EditText timeField) {
        String timePattern = "^([01]\\d|2[0-3]):[0-5]\\d - ([01]\\d|2[0-3]):[0-5]\\d$"; // HH:mm - HH:mm
        String timeInput = timeField.getText().toString().trim();

        if (!timeInput.matches(timePattern)) {
            timeField.setError("Incorrect format!");
            timeField.requestFocus();
            return false;
        }

        // check if start time is before end time
        String[] times = timeInput.split(" - ");
        String startTime = times[0];
        String endTime = times[1];

        try {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            timeFormat.setLenient(false);

            long startMillis = timeFormat.parse(startTime).getTime();
            long endMillis = timeFormat.parse(endTime).getTime();

            if (startMillis >= endMillis) {
                timeField.setError("Start time must be earlier than end time!");
                timeField.requestFocus();
                return false;
            }
        } catch (ParseException e) {
            timeField.setError("Invalid time format!");
            timeField.requestFocus();
            return false;
        }

        return true;
    }



}