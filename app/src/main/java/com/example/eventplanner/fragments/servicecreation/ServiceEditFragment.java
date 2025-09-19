package com.example.eventplanner.fragments.servicecreation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventplanner.R;
import com.example.eventplanner.viewmodels.ServiceEditViewModel;

public class ServiceEditFragment extends Fragment {

    private ServiceEditViewModel viewModel;
    private static final String ARG_SERVICE_ID = "service_id";

    public ServiceEditFragment() {
        // Obavezan prazan konstruktor za fragmente
    }

    public static ServiceEditFragment newInstance(Long serviceId) {
        ServiceEditFragment fragment = new ServiceEditFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_SERVICE_ID, serviceId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ServiceEditViewModel.class);
        if (getArguments() != null) {
            Long serviceId = getArguments().getLong(ARG_SERVICE_ID);
            viewModel.fetchService(serviceId);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service_edit, container, false);

        AppCompatButton editButton = view.findViewById(R.id.saveServiceEdit);
        AppCompatButton deleteButton = view.findViewById(R.id.saveServiceDelete);
        View closeFormButton = view.findViewById(R.id.imageView5);

        editButton.setOnClickListener(v -> {
            // viewModel.editService();
            Toast.makeText(getContext(), R.string.service_edited, Toast.LENGTH_SHORT).show();
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        deleteButton.setOnClickListener(v -> {
            // viewModel.deleteService();
            Toast.makeText(getContext(), R.string.service_deleted, Toast.LENGTH_SHORT).show();
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        closeFormButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }
}