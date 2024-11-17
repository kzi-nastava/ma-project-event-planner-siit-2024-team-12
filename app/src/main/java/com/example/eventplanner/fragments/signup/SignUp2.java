package com.example.eventplanner.fragments.signup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventplanner.R;
import com.example.eventplanner.activities.SignUpActivity;

public class SignUp2 extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up2, container, false);

        Button nextButton = view.findViewById(R.id.next2);
        Button backButton = view.findViewById(R.id.back2);

        nextButton.setOnClickListener(v -> {
            if (getActivity() instanceof SignUpActivity) {
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
