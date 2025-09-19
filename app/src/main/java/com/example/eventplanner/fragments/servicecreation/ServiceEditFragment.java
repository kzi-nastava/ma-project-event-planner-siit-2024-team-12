package com.example.eventplanner.fragments.servicecreation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.dto.solutionservice.GetServiceDTO;
import com.example.eventplanner.viewmodels.ServiceEditViewModel;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.Locale;

public class ServiceEditFragment extends Fragment {

    private ServiceEditViewModel viewModel;
    private static final String ARG_SERVICE_ID = "service_id";

    private ShapeableImageView serviceImage;
    private EditText serviceNameEditText;
    private EditText servicePriceEditText;
    private EditText serviceDiscountEditText;
    private EditText serviceCategoryEditText;
    private Spinner eventTypeSpinner;
    private Spinner reservationTypeSpinner;
    private Spinner visibilitySpinner;
    private Spinner availabilitySpinner;
    private EditText descriptionEditText;

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

        serviceImage = view.findViewById(R.id.shapeableImageView);
        serviceNameEditText = view.findViewById(R.id.editTextServiceName);
        servicePriceEditText = view.findViewById(R.id.editTextServicePrice);
        serviceDiscountEditText = view.findViewById(R.id.editTextServiceDiscount);
        serviceCategoryEditText = view.findViewById(R.id.editTextServiceCategory);
        eventTypeSpinner = view.findViewById(R.id.spinnerEventType);
        reservationTypeSpinner = view.findViewById(R.id.spinnerReservationType);
        visibilitySpinner = view.findViewById(R.id.spinnerVisibility);
        availabilitySpinner = view.findViewById(R.id.spinnerAvailability);
        descriptionEditText = view.findViewById(R.id.editTextDescription);

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
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Kreiranje Observer-a za serviceData
        viewModel.getServiceData().observe(getViewLifecycleOwner(), service -> {
            if (service != null) {
                populateFields(service);
            }
        });
    }

    /**
     * Puni View komponente sa podacima iz GetServiceDTO objekta.
     * @param service Objekat sa podacima o usluzi.
     */
    private void populateFields(GetServiceDTO service) {
        // Popunjavanje EditText polja
        serviceNameEditText.setText(service.getName());
        servicePriceEditText.setText(String.format(Locale.getDefault(), "%.2f", service.getPrice()));
        serviceDiscountEditText.setText(String.format(Locale.getDefault(), "%.2f", service.getDiscount()));
        descriptionEditText.setText(service.getDescription());

        // Popunjavanje ImageView (uz pomoć Glide biblioteke)
        if (service.getImageUrl() != null && !service.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(service.getImageUrl())
                    .placeholder(R.drawable.service1) // slika za učitavanje
                    .into(serviceImage);
        }

        // Popunjavanje Spinnera
        // Napomena: Za spinnere, prvo morate dobiti ArrayAdapter,
        // a zatim pronaći indeks itema i postaviti selekciju.

        // Za kategoriju, koja je EditText, postavite ime
        if (service.getCategory() != null) {
            serviceCategoryEditText.setText(service.getCategory());
        }

        // Primer za spinner (potrebno je imati adapter i listu opcija)
        // String[] reservationOptions = getResources().getStringArray(R.array.service_reservation_confirmation_spinner);
        // int reservationIndex = Arrays.asList(reservationOptions).indexOf(service.getReservationType().toString());
        // if (reservationIndex >= 0) {
        //    reservationTypeSpinner.setSelection(reservationIndex);
        // }
    }
}