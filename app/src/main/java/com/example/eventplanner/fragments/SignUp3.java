package com.example.eventplanner.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventplanner.R;
import com.example.eventplanner.activities.SignUpActivity;
import com.example.eventplanner.activities.BusinessRegistrationActivity;

public class SignUp3 extends Fragment {

    private String selectedRole = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up3, container, false);

        Button backButton = view.findViewById(R.id.back3);
        Button submitButton = view.findViewById(R.id.next);
        Spinner mySpinner = view.findViewById(R.id.mySpinner);


        String[] rolesArray = getResources().getStringArray(R.array.roles_spinner);
        String providerRole = rolesArray[1];

        String registrationSuccess = getString(R.string.registration_success);


        backButton.setOnClickListener(v -> {
            if (getActivity() instanceof SignUpActivity) {
                ((SignUpActivity) getActivity()).previousPage();
            }
        });



        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRole = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedRole = "";
            }
        });


        submitButton.setOnClickListener(v -> {
            if (selectedRole.equalsIgnoreCase(providerRole)) {
                Intent intent = new Intent(getActivity(), BusinessRegistrationActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(getActivity(), registrationSuccess, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
