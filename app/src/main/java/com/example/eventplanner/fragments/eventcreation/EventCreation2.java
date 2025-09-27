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
import com.example.eventplanner.utils.ValidationUtils;
import com.example.eventplanner.activities.event.EventCreationActivity;
import com.example.eventplanner.viewmodels.EventCreationViewModel;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EventCreation2 extends Fragment {
    private View view;

    private EventCreationViewModel viewModel;
    private Spinner privacySpinner;


    public EventCreation2() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_event_creation2, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(EventCreationViewModel.class);

        setUpBackButton();
        setUpPrivacySpinner();
        setUpAgendaButton();
        setUpLocationButton();
        setUpDatePicker();

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


    private void setUpBackButton() {
        Button backButton = view.findViewById(R.id.back2);
        backButton.setOnClickListener(v -> {
            if (getActivity() instanceof EventCreationActivity) {
                ((EventCreationActivity) getActivity()).previousPage();
            }
        });
    }


    private void setUpPrivacySpinner() {
        privacySpinner = view.findViewById(R.id.mySpinner);
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
    }


    private void setUpAgendaButton() {
        Button agendaBtn = view.findViewById(R.id.agendaBtn);
        agendaBtn.setOnClickListener(v -> {
            TextInputEditText dateField = view.findViewById(R.id.dateEditText);

            // check if date field is empty
            if (!ValidationUtils.isFieldValid(dateField, "Date is required!")) return;
            // check date format
            if (!ValidationUtils.isDateValid(dateField)) return;

            if (!viewModel.isLocationSet()) {
                Toast.makeText(getActivity(), "Fill out location form!", Toast.LENGTH_SHORT).show();
                return;
            }

            String date = dateField.getText().toString();
            String privacy = privacySpinner.getSelectedItem().toString();

            viewModel.updateEventAttributes("privacy", privacy);
            view.findViewById(R.id.frame).setVisibility(View.GONE);

            AgendaFragment agendaFragment = new AgendaFragment();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.main, agendaFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
    }


    private void setUpLocationButton() {
        Button locationBtn = view.findViewById(R.id.locationButton);
        locationBtn.setOnClickListener(v -> {
            openLocationForm(view);
        });
    }

    private void openDatePicker(TextInputEditText dateEditText) {
        long tomorrow = MaterialDatePicker.todayInUtcMilliseconds() + 24 * 60 * 60 * 1000;

        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.from(tomorrow));

        MaterialDatePicker<Long> datePicker =
                MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select date")
                        .setSelection(tomorrow)
                        .setCalendarConstraints(constraintsBuilder.build())
                        .build();

        datePicker.show(getParentFragmentManager(), "date_picker");

        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String formattedDate = sdf.format(new Date(selection));
            dateEditText.setText(formattedDate);

            TextInputLayout dateLayout = view.findViewById(R.id.dateLayout);
            dateLayout.setHintEnabled(false);

            if (viewModel != null) {
                viewModel.updateEventAttributes("date", formattedDate);
            }
        });
    }


    private void setUpDatePicker() {
        TextInputLayout dateLayout = view.findViewById(R.id.dateLayout);
        TextInputEditText dateEditText = view.findViewById(R.id.dateEditText);

        dateEditText.setOnClickListener(v -> {
            openDatePicker(dateEditText);
        });

        dateLayout.setEndIconOnClickListener(v -> {
            openDatePicker(dateEditText);
        });
    }

}