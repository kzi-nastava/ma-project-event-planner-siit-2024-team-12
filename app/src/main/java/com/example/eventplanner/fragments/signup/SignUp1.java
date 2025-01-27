package com.example.eventplanner.fragments.signup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventplanner.R;
import com.example.eventplanner.activities.auth.SignUpActivity;
import com.example.eventplanner.viewmodels.SignUpViewModel;

public class SignUp1 extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up1, container, false);

        SignUpViewModel viewModel = new ViewModelProvider(requireActivity()).get(SignUpViewModel.class);

        Button nextButton = view.findViewById(R.id.next1);
        nextButton.setOnClickListener(v -> {
            if (getActivity() instanceof SignUpActivity) {
                EditText emailField = view.findViewById(R.id.email);
                EditText passwordField = view.findViewById(R.id.password);
                EditText confirmationField = view.findViewById(R.id.passwordConfirmation);

                if (!validateField(emailField, "Email is required!")) return;
                if (!validateEmailFormat(emailField)) return;
                if (!validateField(passwordField, "Password is required!")) return;
                if (!validateField(confirmationField, "Confirmation is required!")) return;
                if (!validatePasswordConfirmation(passwordField, confirmationField)) return;

                // if valid, save input data
                viewModel.updateSignUpAttributes("email", emailField.getText().toString());
                viewModel.updateSignUpAttributes("password", passwordField.getText().toString());


                ((SignUpActivity) getActivity()).nextPage();
            }
        });

        return view;
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


    private boolean validateEmailFormat(EditText field) {
        String email = field.getText().toString().trim();
        String emailPattern = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

        if (!email.matches(emailPattern)) {
            field.setError("Invalid email format!");
            field.requestFocus();
            return false;
        }
        return true;
    }


    private boolean validatePasswordConfirmation(EditText passwordField, EditText confirmationField) {
        String password = passwordField.getText().toString().trim();
        String confirmation = confirmationField.getText().toString().trim();

        if (!password.equals(confirmation)) {
            confirmationField.setError("Passwords do not match!");
            confirmationField.requestFocus();
            return false;
        }
        return true;
    }


}
