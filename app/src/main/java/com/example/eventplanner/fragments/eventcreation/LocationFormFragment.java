package com.example.eventplanner.fragments.eventcreation;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.eventplanner.R;
import com.example.eventplanner.ValidationUtils;
import com.example.eventplanner.dto.location.CreateLocationDTO;
import com.example.eventplanner.viewmodels.EventCreationViewModel;


public class LocationFormFragment extends DialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_location_form, container, false);

        EventCreationViewModel viewModel = new ViewModelProvider(requireActivity()).get(EventCreationViewModel.class);

        ImageView exit = view.findViewById(R.id.exitBtn);
        exit.setOnClickListener(v -> {
            closeForm(view);
        });

        Button nextBtn = view.findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(v -> {
            EditText venueField = view.findViewById(R.id.venue);
            EditText cityField = view.findViewById(R.id.city);
            EditText countryField = view.findViewById(R.id.country);
            EditText addressField = view.findViewById(R.id.address);

            // validate fields
            if (!ValidationUtils.isFieldValid(venueField, "Venue is required!")) return;
            if (!ValidationUtils.isFieldValid(addressField, "Address is required!")) return;
            if (!ValidationUtils.isFieldValid(cityField, "City is required!")) return;
            if (!ValidationUtils.isFieldValid(countryField, "Country is required!")) return;

            // if valid, save
            String venue = venueField.getText().toString();
            String city = cityField.getText().toString();
            String country = countryField.getText().toString();
            String address = addressField.getText().toString();

            CreateLocationDTO locationDTO = new CreateLocationDTO(venue, address, city, country);

            viewModel.updateLocation(locationDTO);

            dismiss();

            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.popBackStack();

        });

        return view;
    }

    public void closeForm(View view) {
        dismiss();
    }




}