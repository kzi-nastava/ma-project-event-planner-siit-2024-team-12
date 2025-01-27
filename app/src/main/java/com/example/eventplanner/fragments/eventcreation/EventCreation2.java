package com.example.eventplanner.fragments.eventcreation;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventplanner.R;
import com.example.eventplanner.activities.event.EventCreationActivity;
import com.example.eventplanner.viewmodels.EventCreationViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class EventCreation2 extends Fragment {

    public EventCreation2() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_creation2, container, false);

        EventCreationViewModel viewModel = new ViewModelProvider(requireActivity()).get(EventCreationViewModel.class);

        Button backButton = view.findViewById(R.id.back2);
        backButton.setOnClickListener(v -> {
            if (getActivity() instanceof EventCreationActivity) {
                ((EventCreationActivity) getActivity()).previousPage();
            }
        });


        Spinner privacySpinner = view.findViewById(R.id.mySpinner);
        TextView sendInvitationsText = view.findViewById(R.id.sendInvitationsText);

        privacySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = parent.getItemAtPosition(position).toString();

                // load privacy types
                String[] privacyTypes = getResources().getStringArray(R.array.privacy_types);
                String closedValue = privacyTypes[1]; // privacy type CLOSED is at index 1

                // show send invitations option for closed events
                if (closedValue.equals(selectedOption)) {
                    sendInvitationsText.setVisibility(View.VISIBLE);
                } else {
                    sendInvitationsText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        Button agendaBtn = view.findViewById(R.id.agendaBtn);
        agendaBtn.setOnClickListener(v -> {
            EditText dateField = view.findViewById(R.id.date);
            // check if date field is empty
            if (!validateField(dateField, "Date is required!")) return;

            // check date format
            if (!validateDateFormat(dateField)) return;

            if (!viewModel.isLocationSet()) {
                Toast.makeText(getActivity(), "Fill out location form!", Toast.LENGTH_SHORT).show();
                return;
            }

            String date = dateField.getText().toString();

            String privacy = privacySpinner.getSelectedItem().toString();

            viewModel.updateEventAttributes("date", date);
            viewModel.updateEventAttributes("privacy", privacy);


            view.findViewById(R.id.frame).setVisibility(View.GONE);

            AgendaFragment agendaFragment = new AgendaFragment();

            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.main, agendaFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });


        Button locationBtn = view.findViewById(R.id.locationButton);
        locationBtn.setOnClickListener(v -> {
            openLocationForm(view);
        });

        return view;
    }



    private void openLocationForm(View view) {
        LocationFormFragment locationFormFragment = new LocationFormFragment();
        locationFormFragment.show(getChildFragmentManager(), "locationDetails");
    }


    public void closeForm(View view) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.remove(this);
        transaction.commit();
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



    private boolean validateDateFormat(EditText field) {
        String datePattern = "^\\d{4}-\\d{2}-\\d{2}$"; // format yyyy-mm-dd
        String date = field.getText().toString().trim();

        if (!date.matches(datePattern)) {
            field.setError("Incorrect format!");
            field.requestFocus();
            return false;
        }

        // check if date is valid ( e.g. there is no February 30 )
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setLenient(false);

            long inputDateMillis = dateFormat.parse(date).getTime();
            long currentDateMillis = System.currentTimeMillis();

            // check if entered date is in the past
            if (inputDateMillis < currentDateMillis) {
                field.setError("Date cannot be in the past!");
                field.requestFocus();
                return false;
            }
        } catch (ParseException e) {
            field.setError("Invalid date!");
            field.requestFocus();
            return false;
        }

        return true;
    }

}
