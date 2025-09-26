package com.example.eventplanner.fragments.servicereservation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

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

    private static final int PAGE_SIZE = 6;

    private RecyclerView recyclerReservations;
    private ImageButton prevPageButton;
    private ImageButton nextPageButton;
    private TextView pageNumber;

    private List<GetServiceReservationDTO> allReservations = new ArrayList<>();
    private List<GetServiceReservationDTO> reservations = new ArrayList<>();

    private int currentPage = 0;
    private int totalPages = 0;

    private ServiceReservationsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_service_reservations_management, container, false);

        recyclerReservations = v.findViewById(R.id.recyclerReservations);
        prevPageButton = v.findViewById(R.id.prevPageButton);
        nextPageButton = v.findViewById(R.id.nextPageButton);
        pageNumber = v.findViewById(R.id.pageNumber);

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

        prevPageButton.setOnClickListener(v1 -> navigateToPreviousPage());
        nextPageButton.setOnClickListener(v12 -> navigateToNextPage());

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
                                    String serviceDateString = r.getServiceDate();
                                    if (serviceDateString != null && !serviceDateString.isEmpty()) {
                                        r.setServiceLocalDate(LocalDate.parse(serviceDateString, BACKEND_DATE_FORMATTER));
                                    } else {
                                        r.setServiceLocalDate(null);
                                    }

                                } catch (Exception e) {
                                    r.setEventLocalDate(null);
                                    r.setServiceLocalDate(null);
                                }
                            }

                            reservations.addAll(response.body());
                            adapter.notifyDataSetChanged();
                            allReservations.clear();
                            allReservations.addAll(response.body());

                            currentPage = 0;
                            displayCurrentPage();
                        } else {
                            android.util.Log.e("RESERVATIONS_API", "Error code: " + response.code());
                            updatePaginationUI();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<GetServiceReservationDTO>> call, Throwable t) {
                        android.util.Log.e("RESERVATIONS_API", "Network failure", t);
                        updatePaginationUI();
                    }
                });
    }

    private void displayCurrentPage() {
        int startIndex = currentPage * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, allReservations.size());

        reservations.clear();
        if (startIndex < endIndex) {
            reservations.addAll(allReservations.subList(startIndex, endIndex));
        }

        adapter.notifyDataSetChanged();
        updatePaginationUI();
        recyclerReservations.scrollToPosition(0);
    }

    private void updatePaginationUI() {
        totalPages = (int) Math.ceil((double) allReservations.size() / PAGE_SIZE);

        if (totalPages == 0) totalPages = 1;

        pageNumber.setText(String.format("%d/%d", currentPage + 1, totalPages));

        prevPageButton.setEnabled(currentPage > 0);
        nextPageButton.setEnabled(currentPage < totalPages - 1);

        prevPageButton.setVisibility(totalPages > 1 ? View.VISIBLE : View.GONE);
        nextPageButton.setVisibility(totalPages > 1 ? View.VISIBLE : View.GONE);
        pageNumber.setVisibility(totalPages > 1 ? View.VISIBLE : View.GONE);
    }

    private void navigateToNextPage() {
        if (currentPage < totalPages - 1) {
            currentPage++;
            displayCurrentPage();
        }
    }

    private void navigateToPreviousPage() {
        if (currentPage > 0) {
            currentPage--;
            displayCurrentPage();
        }
    }
}

