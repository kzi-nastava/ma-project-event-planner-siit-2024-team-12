package com.example.eventplanner.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventplanner.R;
import com.example.eventplanner.activities.SignUpActivity;

public class SignUp3 extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up3, container, false);

        Button backButton = view.findViewById(R.id.back3);

        backButton.setOnClickListener(v -> {
            if (getActivity() instanceof SignUpActivity) {
                ((SignUpActivity) getActivity()).previousPage();
            }
        });

        return view;
    }
}
