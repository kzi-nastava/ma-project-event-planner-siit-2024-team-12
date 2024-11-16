package com.example.eventplanner.fragments.businessregistration;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.eventplanner.R;
import com.example.eventplanner.activities.BusinessRegistrationActivity;


public class BusinessRegistration2 extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_business_registration2, container, false);

        String registrationSuccess = getString(R.string.registration_success);

        Button backButton = view.findViewById(R.id.back2);
        Button registerButton = view.findViewById(R.id.register);

        backButton.setOnClickListener(v -> {
            if (getActivity() instanceof BusinessRegistrationActivity) {
                ((BusinessRegistrationActivity) getActivity()).previousPage();
            }
        });

        /*
        registerButton.setOnClickListener(v -> {
            Toast.makeText(getActivity(), registrationSuccess, Toast.LENGTH_SHORT).show();

        });

         */

        return view;

    }
}