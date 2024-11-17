package com.example.eventplanner.fragments.servicecreation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventplanner.R;
import com.example.eventplanner.activities.ServiceEditActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductProviderServices#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductProviderServices extends Fragment {

    private ActivityResultLauncher<Intent> activityResultLauncher;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProductProviderServices() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProductProviderServices.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductProviderServices newInstance(String param1, String param2) {
        ProductProviderServices fragment = new ProductProviderServices();
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
                        // Na primer, možeš pročitati podatke iz Intent-a ovde
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_provider_services, container, false);
        AppCompatButton serviceEditButton = view.findViewById(R.id.service_edit);
        serviceEditButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ServiceEditActivity.class);
            activityResultLauncher.launch(intent);
        });
        return view;
    }
}