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

public class SignUp1 extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up1, container, false);

        Button nextButton = view.findViewById(R.id.next1);
        nextButton.setOnClickListener(v -> {
            if (getActivity() instanceof SignUpActivity) {
                ((SignUpActivity) getActivity()).nextPage();
            }
        });

        return view;
    }
}
