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
import android.widget.ImageButton;

import com.example.eventplanner.R;
import com.example.eventplanner.activities.ServiceCreationActivity;
import com.example.eventplanner.fragments.homepage.HomepageFilterFragment;

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

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ServiceManagement() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
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
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to start ServiceCreationActivity
                Intent intent = new Intent(getActivity(), ServiceCreationActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

            transaction.replace(R.id.services_fragment_container, new ProductProviderServices());
            transaction.replace(R.id.service_filter_fragment_container, new HomepageFilterFragment());

            transaction.commit();
        }
    }
}