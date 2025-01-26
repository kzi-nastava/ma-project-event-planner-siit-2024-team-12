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
import com.example.eventplanner.activities.event.EventCreationActivity;
import com.example.eventplanner.viewmodels.EventCreationViewModel;


public class EventCreation1 extends Fragment {


    public EventCreation1() {
        // Required empty public constructor
    }



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
        nextButton.setOnClickListener(v -> {
            if (getActivity() instanceof EventCreationActivity) {

                // save input data from the form
                EditText nameField = view.findViewById(R.id.name);
                EditText maxGuestsField = view.findViewById(R.id.maxGuests);
                EditText descriptionField = view.findViewById(R.id.description);

                String name = nameField.getText().toString();
                String maxGuests = maxGuestsField.getText().toString();
                String description = descriptionField.getText().toString();

                viewModel.updateEventAttributes("name", name);
                viewModel.updateEventAttributes("maxGuests", maxGuests);
                viewModel.updateEventAttributes("description", description);

                // move to the next form
                ((EventCreationActivity) getActivity()).nextPage();
            }
        });

        return view;
    }
}