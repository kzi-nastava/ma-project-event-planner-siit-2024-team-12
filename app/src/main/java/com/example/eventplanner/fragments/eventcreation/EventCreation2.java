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

import com.example.eventplanner.R;
import com.example.eventplanner.activities.event.EventCreationActivity;
import com.example.eventplanner.viewmodels.EventCreationViewModel;

public class EventCreation2 extends Fragment {

    View view;

    public EventCreation2() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_event_creation2, container, false);

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

                if ("Closed".equals(selectedOption)) {
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
            // save input data
            EditText dateField = view.findViewById(R.id.date);
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



    public void openLocationForm(View view) {
        LocationFormFragment locationFormFragment = new LocationFormFragment();
        locationFormFragment.show(getChildFragmentManager(), "locationDetails");
    }


    public void closeForm(View view) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.remove(this);
        transaction.commit();
    }





}
