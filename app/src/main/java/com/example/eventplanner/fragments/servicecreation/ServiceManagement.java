package com.example.eventplanner.fragments.servicecreation;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.eventplanner.R;
import com.example.eventplanner.fragments.pricelist.PriceListFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ServiceManagement#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ServiceManagement extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ImageButton imgButton;
    private Button priceListButton;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ServiceManagement() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a.java new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ServiceManagement.
     */
    // TODO: Rename and change types and number of parameters
    public static ServiceManagement newInstance(String param1, String param2) {
        ServiceManagement fragment = new ServiceManagement();
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
        View rootView =  inflater.inflate(R.layout.fragment_service_management, container, false);
        imgButton = rootView.findViewById(R.id.imageButton);
        priceListButton = rootView.findViewById(R.id.full_width_button);
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openServiceCreationFragment();
            }
        });

        priceListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPriceListFragment();
            }
        });

        return rootView;
    }

    private void openServiceCreationFragment() {
        ServiceCreationContainer serviceCreationContainerFragment = new ServiceCreationContainer();

        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.homepage_fragment_container, serviceCreationContainerFragment)
                .addToBackStack(null)
                .commit();
    }

    private void openPriceListFragment() {
        PriceListFragment priceListFragment = PriceListFragment.newInstance("service");

        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.homepage_fragment_container, priceListFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

            transaction.replace(R.id.services_fragment_container, new ProductProviderServices());

            transaction.commit();
        }
    }
}