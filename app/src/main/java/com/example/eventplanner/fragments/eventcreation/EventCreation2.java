package com.example.eventplanner.fragments.eventcreation;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.eventplanner.R;
import com.example.eventplanner.activities.auth.SignUpActivity;
import com.example.eventplanner.activities.event.EventCreationActivity;

public class EventCreation2 extends Fragment {


    public EventCreation2() {
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
        View view = inflater.inflate(R.layout.fragment_event_creation2, container, false);

        Button backButton = view.findViewById(R.id.back2);

        backButton.setOnClickListener(v -> {
            if (getActivity() instanceof EventCreationActivity) {
                ((EventCreationActivity) getActivity()).previousPage();
            }
        });

        return view;
    }


}
