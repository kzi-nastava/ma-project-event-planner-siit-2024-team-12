package com.example.eventplanner.fragments.business.businessregistration;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.eventplanner.R;
import com.example.eventplanner.utils.ValidationUtils;
import com.example.eventplanner.viewmodels.BusinessViewModel;


public class BusinessRegistration1 extends Fragment {
    private View view;
    BusinessViewModel viewModel;

   @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       view = inflater.inflate(R.layout.fragment_business_registration1, container, false);
       viewModel = new ViewModelProvider(requireActivity()).get(BusinessViewModel.class);

       Button nextButton = view.findViewById(R.id.nextBtn);

       nextButton.setOnClickListener(v -> {
           if (saveFormData()) {
               Fragment parent = requireActivity()
                       .getSupportFragmentManager()
                       .findFragmentById(R.id.main_fragment_container);

               if (parent instanceof BusinessRegistrationFragment) {
                   ((BusinessRegistrationFragment) parent).nextPage();
               }

           }
       });

       return view;
   }


   private boolean saveFormData() {
       EditText nameField = view.findViewById(R.id.name);
       EditText emailField = view.findViewById(R.id.email);
       EditText addressField = view.findViewById(R.id.address);

       // validate input data
        if (!ValidationUtils.isFieldValid(nameField, "Company name is required!")) return false;
        if (!ValidationUtils.isFieldValid(emailField, "Email is required")) return false;
        if (!ValidationUtils.isEmailValid(emailField)) return false;
        if (!ValidationUtils.isFieldValid(addressField, "Address is required")) return false;

        // if valid, save
       String name = nameField.getText().toString().trim();
       String email = emailField.getText().toString().trim();
       String address = addressField.getText().toString().trim();

       viewModel.update("name", name);
       viewModel.update("email", email);
       viewModel.update("address", address);

       return true;

   }

}