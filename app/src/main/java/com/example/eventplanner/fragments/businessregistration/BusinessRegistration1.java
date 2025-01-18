package com.example.eventplanner.fragments.businessregistration;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.eventplanner.R;
import com.example.eventplanner.activities.business.BusinessRegistrationActivity;


public class BusinessRegistration1 extends Fragment {

   @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_business_registration1, container, false);

       Button nextButton = view.findViewById(R.id.next1);

       nextButton.setOnClickListener(v -> {
           if (getActivity() instanceof BusinessRegistrationActivity) {
               ((BusinessRegistrationActivity) getActivity()).nextPage();
           }
       });

       return view;

   }
}