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
import com.example.eventplanner.activities.auth.SignUpActivity;
import com.example.eventplanner.viewmodels.SignUpViewModel;

public class SignUp2 extends Fragment {

    private static final String ARG_IS_UPGRADE = "is_upgrade";
    private static final String ARG_EMAIL = "email";

    public static SignUp2 newInstance(boolean isUpgrade, String email) {
        SignUp2 fragment = new SignUp2();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_UPGRADE, isUpgrade);
        args.putString(ARG_EMAIL, email);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up2, container, false);

        SignUpViewModel viewModel = new ViewModelProvider(requireActivity()).get(SignUpViewModel.class);

        Button nextButton = view.findViewById(R.id.next2);
        Button backButton = view.findViewById(R.id.back2);

        nextButton.setOnClickListener(v -> {
            if (getActivity() instanceof SignUpActivity) {
                EditText nameField = view.findViewById(R.id.name);
                EditText surnameField = view.findViewById(R.id.surname);

                if (!ValidationUtils.isFieldValid(nameField, "Name is required!")) return;
                if (!ValidationUtils.isFieldValid(surnameField, "Surname is required!")) return;

                viewModel.updateSignUpAttributes("name", nameField.getText().toString());
                viewModel.updateSignUpAttributes("surname", surnameField.getText().toString());

                ((SignUpActivity) getActivity()).nextPage();
            }
        });

        backButton.setOnClickListener(v -> {
            if (getActivity() instanceof SignUpActivity) {
                ((SignUpActivity) getActivity()).previousPage();
            }
        });

        return view;
    }



}
