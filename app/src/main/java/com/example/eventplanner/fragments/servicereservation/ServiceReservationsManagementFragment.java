package com.example.eventplanner.fragments.servicereservation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.ServiceReservationsAdapter;
import com.example.eventplanner.dto.servicereservation.GetServiceReservationDTO;
import com.example.eventplanner.utils.ClientUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceReservationsManagementFragment extends Fragment {

    private RecyclerView recyclerReservations;
    private ServiceReservationsAdapter adapter;
    private List<GetServiceReservationDTO> reservations = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_service_reservations_management, container, false);

        recyclerReservations = v.findViewById(R.id.recyclerReservations);
        recyclerReservations.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ServiceReservationsAdapter(reservations, reservation -> {
            Bundle bundle = new Bundle();
            bundle.putLong("reservationId", reservation.getReservationId());

            ServiceReservationDetailsFragment fragment = new ServiceReservationDetailsFragment();
            fragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        recyclerReservations.setAdapter(adapter);

        loadReservations();

        return v;
    }

    private void loadReservations() {
        String authorization = ClientUtils.getAuthorization(getContext());

        final DateTimeFormatter BACKEND_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        ClientUtils.serviceReservationService.getReservationsForOrganizer(authorization)
                .enqueue(new Callback<List<GetServiceReservationDTO>>() {
                    @Override
                    public void onResponse(Call<List<GetServiceReservationDTO>> call, Response<List<GetServiceReservationDTO>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            reservations.clear();
                            for (GetServiceReservationDTO r : response.body()) {
                                try {
                                    // parse eventDate
                                    if (r.getEventDate() != null) {
                                        r.setEventLocalDate(LocalDate.parse(r.getEventDate(), BACKEND_DATE_FORMATTER));
                                    }
                                    // parse serviceDate
                                    if (r.getServiceDate() != null) {
                                        r.setServiceLocalDate(LocalDate.parse(r.getServiceDate(), BACKEND_DATE_FORMATTER));
                                    }

                                    android.util.Log.e("RESERVATIONS_PARSE", "Parsed event date: " + r.getEventLocalDate() + ", service date: " + r.getServiceLocalDate());

                                } catch (Exception e) {
                                    android.util.Log.e("RESERVATIONS_PARSE", "Failed to parse date string for item: " + r.getEventDate() + " or " + r.getServiceDate(), e);
                                    r.setEventLocalDate(null);
                                    r.setServiceLocalDate(null);
                                }
                            }

                            reservations.addAll(response.body());
                            adapter.notifyDataSetChanged();
                        } else {
                            android.util.Log.e("RESERVATIONS_API", "Error code: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<GetServiceReservationDTO>> call, Throwable t) {
                        android.util.Log.e("RESERVATIONS_API", "Network failure", t);
                    }
                });
    }


    }

