package com.example.eventplanner.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.eventplanner.R;
import com.example.eventplanner.activities.BusinessRegistrationActivity;


public class BusinessRegistration2 extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_business_registration2, container, false);

        Button backButton = view.findViewById(R.id.back2);

        backButton.setOnClickListener(v -> {
            if (getActivity() instanceof BusinessRegistrationActivity) {
                ((BusinessRegistrationActivity) getActivity()).previousPage();
            }
        });

        return view;

    }
}