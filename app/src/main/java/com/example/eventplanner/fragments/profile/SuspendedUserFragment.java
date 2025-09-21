package com.example.eventplanner.fragments.profile;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventplanner.R;

public class SuspendedUserFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_suspended, container, false);

        Bundle args = getArguments();
        if (args != null) {
            String days = args.getString("days");
            String hours = args.getString("hours");
            String minutes = args.getString("minutes");

            TextView suspendedTime = view.findViewById(R.id.suspended_time);
            String remainingTime = String.format("Time remaining until reactivation: %s days, %s hours, %s minutes", days, hours, minutes);
            suspendedTime.setText(remainingTime);
        }

        return view;
    }
}