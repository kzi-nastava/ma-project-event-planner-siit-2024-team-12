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
import com.example.eventplanner.utils.ValidationUtils;
import com.example.eventplanner.fragments.auth.SignUpFragment;
import com.example.eventplanner.viewmodels.SignUpViewModel;

public class SignUp1 extends Fragment {

    private static final String ARG_IS_UPGRADE = "is_upgrade";
    private static final String ARG_EMAIL = "email";

    private boolean isUpgrade = false;
    private String userEmail;

    public static SignUp1 newInstance(boolean isUpgrade, String email) {
        SignUp1 fragment = new SignUp1();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_UPGRADE, isUpgrade);
        args.putString(ARG_EMAIL, email);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up1, container, false);

        if (getArguments() != null) {
            isUpgrade = getArguments().getBoolean(ARG_IS_UPGRADE, false);
            userEmail = getArguments().getString(ARG_EMAIL);
        }

        SignUpViewModel viewModel = new ViewModelProvider(requireActivity()).get(SignUpViewModel.class);

        Button nextButton = view.findViewById(R.id.next1);
        EditText emailField = view.findViewById(R.id.email);
        EditText passwordField = view.findViewById(R.id.password);
        EditText confirmationField = view.findViewById(R.id.passwordConfirmation);

        if (isUpgrade) {
            emailField.setText(userEmail);
            emailField.setEnabled(false);
            viewModel.updateSignUpAttributes("email", userEmail);
            viewModel.setUpgradeMode(true);
        } else {
            emailField.setEnabled(true);
        }

        nextButton.setOnClickListener(v -> {
            SignUpFragment parent = (SignUpFragment) getParentFragment();

            if (parent != null) {

                if (!isUpgrade) {
                    if (!ValidationUtils.isFieldValid(emailField, "Email is required!")) return;
                    if (!ValidationUtils.isEmailValid(emailField)) return;
                }

                if (!ValidationUtils.isFieldValid(passwordField, "Password is required!")) return;
                if (!ValidationUtils.isFieldValid(confirmationField, "Confirmation is required!")) return;
                if (!ValidationUtils.isMatchingPassword(passwordField, confirmationField)) return;

                if (!isUpgrade) { viewModel.updateSignUpAttributes("email", emailField.getText().toString()); }
                viewModel.updateSignUpAttributes("password", passwordField.getText().toString());

                parent.nextPage();
            }
        });

        return view;
    }

}
