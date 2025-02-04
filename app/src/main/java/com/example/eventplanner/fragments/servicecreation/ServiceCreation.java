package com.example.eventplanner.fragments.servicecreation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.eventplanner.R;
import com.example.eventplanner.activities.solutioncategory.CategoryCreationActivity;
import com.example.eventplanner.activities.service.ServiceCreationActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ServiceCreation#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ServiceCreation extends Fragment {

    private ActivityResultLauncher<Intent> activityResultLauncher;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ServiceCreation() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a.java new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ServiceCreation.
     */
    // TODO: Rename and change types and number of parameters
    public static ServiceCreation newInstance(String param1, String param2) {
        ServiceCreation fragment = new ServiceCreation();
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

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Obradi rezultat ovde ako je potrebno
                        Intent data = result.getData();
                        // Na primer, možeš pročitati podatke iz Intent-a.java ovde
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_service_creation, container, false);
        Button nextButton = view.findViewById(R.id.nextServiceCreate);
        ImageButton newCategoryButton = view.findViewById(R.id.dugme);

        nextButton.setOnClickListener(v -> {
            if (getActivity() instanceof ServiceCreationActivity) {
                ((ServiceCreationActivity) getActivity()).nextPage();
            }
        });

        newCategoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CategoryCreationActivity.class);
            activityResultLauncher.launch(intent);
        });




        return view;
    }
}