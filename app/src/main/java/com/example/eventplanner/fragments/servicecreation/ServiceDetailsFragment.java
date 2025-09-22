package com.example.eventplanner.fragments.servicecreation;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.eventplanner.BuildConfig;
import com.example.eventplanner.R;
import com.example.eventplanner.dto.solutionservice.GetServiceDTO;
import com.example.eventplanner.utils.ClientUtils;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceDetailsFragment extends Fragment {

    private static final String ARG_SERVICE_ID = "service_id";
    private Long serviceId;

    private ImageView serviceImage;
    private TextView serviceTitle;
    private EditText name, city, price, discount, availability, reservationDeadline, cancellationDeadline, description;
    private Button chatButton, bookServiceButton;
    private ImageView exitButton;

    public static ServiceDetailsFragment newInstance(Long serviceId) {
        ServiceDetailsFragment fragment = new ServiceDetailsFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_SERVICE_ID, serviceId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            serviceId = getArguments().getLong(ARG_SERVICE_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service_details, container, false);

        serviceImage = view.findViewById(R.id.serviceImage);
        serviceTitle = view.findViewById(R.id.serviceTitle);
        name = view.findViewById(R.id.name);
        city = view.findViewById(R.id.city);
        price = view.findViewById(R.id.price);
        discount = view.findViewById(R.id.discount);
        availability = view.findViewById(R.id.availability);
        reservationDeadline = view.findViewById(R.id.reservationDeadline);
        cancellationDeadline = view.findViewById(R.id.cancellationDeadline);
        description = view.findViewById(R.id.description);
        chatButton = view.findViewById(R.id.chatButton);
        bookServiceButton = view.findViewById(R.id.bookServiceButton);
        exitButton = view.findViewById(R.id.exitBtn);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchServiceDetails();

        exitButton.setOnClickListener(v -> closeForm());
        chatButton.setOnClickListener(v -> Toast.makeText(getContext(), "Chat feature not yet implemented.", Toast.LENGTH_SHORT).show());
        bookServiceButton.setOnClickListener(v -> Toast.makeText(getContext(), "Booking feature not yet implemented.", Toast.LENGTH_SHORT).show());
    }

    private void fetchServiceDetails() {
        // Dodajte ovaj red da provjerite da li je ID ispravan
        if (serviceId != null) {
            Log.d("ServiceDetailsFragment", "Received service ID: " + serviceId);
        } else {
            Log.e("ServiceDetailsFragment", "Received service ID is NULL!");
        }

        String authorization = ClientUtils.getAuthorization(getContext());
        if (authorization == null) {
            Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
            return;
        }

        ClientUtils.serviceSolutionService.getService(authorization, serviceId).enqueue(new Callback<GetServiceDTO>() {
            @Override
            public void onResponse(Call<GetServiceDTO> call, Response<GetServiceDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    populateUI(response.body());
                } else {
                    // Ispis greške u log
                    if (response.errorBody() != null) {
                        try {
                            String error = response.errorBody().string();
                            Log.e("ServiceDetailsFragment", "API Error: " + response.code() + " " + error);
                        } catch (IOException e) {
                            Log.e("ServiceDetailsFragment", "Error reading error body", e);
                        }
                    } else {
                        Log.e("ServiceDetailsFragment", "API Error: " + response.code() + " (No error body)");
                    }

                    Toast.makeText(getContext(), "Failed to fetch service details.", Toast.LENGTH_SHORT).show();
                    closeForm();
                }
            }

            @Override
            public void onFailure(Call<GetServiceDTO> call, Throwable t) {
                // Ispis greške u log
                Log.e("ServiceDetailsFragment", "Network error", t);
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                closeForm();
            }
        });
    }

    private void populateUI(GetServiceDTO service) {
        serviceTitle.setText(service.getName());
        name.setText(service.getName());
        city.setText(service.getCity());
        price.setText(String.format("%s $", service.getPrice()));
        discount.setText(String.format("%d%%", service.getDiscount().intValue()));
        availability.setText(service.getAvailable() ? "Available" : "Unavailable");
        reservationDeadline.setText(String.format("%d days", service.getReservationDeadline()));
        cancellationDeadline.setText(String.format("%d days", service.getCancellationDeadline()));
        description.setText(service.getDescription());

        loadServiceImage(service.getImageUrl());
    }

    private void loadServiceImage(String imageUrl) {
        if (getContext() != null && isAdded() && imageUrl != null && !imageUrl.isEmpty()) {
            String fullUrl = "http://" + BuildConfig.IP_ADDR + ":8080" + imageUrl;

            Glide.with(getContext())
                    .load(fullUrl)
                    .centerCrop()
                    .placeholder(R.drawable.service1)
                    .error(R.drawable.service1)
                    .into(serviceImage);
        }
    }

    private void closeForm() {
        if (getParentFragmentManager() != null) {
            getParentFragmentManager().popBackStack();
        }
    }
}