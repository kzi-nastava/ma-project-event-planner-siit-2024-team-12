package com.example.eventplanner.fragments.eventcreation;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.eventplanner.R;
import com.example.eventplanner.utils.ValidationUtils;
import com.example.eventplanner.activities.event.EventCreationActivity;
import com.example.eventplanner.viewmodels.EventCreationViewModel;


public class EventCreation1 extends Fragment {

    public EventCreation1() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_creation1, container, false);

        EventCreationViewModel viewModel = new ViewModelProvider(requireActivity()).get(EventCreationViewModel.class);

        Button nextButton = view.findViewById(R.id.nextBtn);
        EditText nameField = view.findViewById(R.id.name);
        EditText maxGuestsField = view.findViewById(R.id.maxGuests);
        EditText descriptionField = view.findViewById(R.id.description);

        nextButton.setOnClickListener(v -> {
            if (getActivity() instanceof EventCreationActivity) {

                // validate input data
                if (!ValidationUtils.isFieldValid(nameField, "Name is required!")) return;
                if (!ValidationUtils.isFieldValid(maxGuestsField, "Max number is required!")) return;
                if (!ValidationUtils.isNumberValid(maxGuestsField)) return;
                if (!ValidationUtils.isFieldValid(descriptionField, "Description is required!")) return;

                // save validated data
                viewModel.updateEventAttributes("name", nameField.getText().toString());
                viewModel.updateEventAttributes("maxGuests", maxGuestsField.getText().toString());
                viewModel.updateEventAttributes("description", descriptionField.getText().toString());

                // move to the next form
                ((EventCreationActivity) getActivity()).nextPage();
            }
        });

        return view;
    }


}