package com.example.eventplanner.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.eventplanner.R;
import com.example.eventplanner.activities.BusinessRegistrationActivity;
import com.example.eventplanner.activities.ServiceCreationActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ServiceCreation4#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ServiceCreation4 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ServiceCreation4() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ServiceCreation4.
     */
    // TODO: Rename and change types and number of parameters
    public static ServiceCreation4 newInstance(String param1, String param2) {
        ServiceCreation4 fragment = new ServiceCreation4();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_service_creation4, container, false);
        Button backButton = view.findViewById(R.id.backServiceCreate4);
        Button submitButton = view.findViewById(R.id.submitService);

        String ServiceCreatedMessage = getString(R.string.service_created);

        backButton.setOnClickListener(v -> {
            if (getActivity() instanceof ServiceCreationActivity) {
                ((ServiceCreationActivity) getActivity()).previousPage();
            }
        });

        submitButton.setOnClickListener(v -> {
            Toast.makeText(getActivity(), ServiceCreatedMessage, Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}