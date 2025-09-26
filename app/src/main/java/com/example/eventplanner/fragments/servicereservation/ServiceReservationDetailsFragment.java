package com.example.eventplanner.fragments.servicereservation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.eventplanner.R;
import com.example.eventplanner.dto.servicereservation.GetServiceReservationDTO;
import com.example.eventplanner.utils.ClientUtils;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.time.format.DateTimeFormatter;
import java.util.Map;

public class ServiceReservationDetailsFragment extends Fragment {

    private TextView textEvent, textService, textDate, textReservationMadeOn,
            textCancellationDeadline, textFinalAmount, textTimeFrom, textTimeTo, textStatus;
    private Button buttonCancel;

    private Long reservationId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service_reservation, container, false);

        textEvent = view.findViewById(R.id.textEvent);
        textService = view.findViewById(R.id.textService);
        textDate = view.findViewById(R.id.textDate);
        textReservationMadeOn = view.findViewById(R.id.textReservationMadeOn);
        textCancellationDeadline = view.findViewById(R.id.textCancellationDeadline);
        textFinalAmount = view.findViewById(R.id.textFinalAmount);
        textTimeFrom = view.findViewById(R.id.textTimeFrom);
        textTimeTo = view.findViewById(R.id.textTimeTo);
        textStatus = view.findViewById(R.id.textStatus);
        buttonCancel = view.findViewById(R.id.buttonCancelReservation);

        if (getArguments() != null) {
            reservationId = getArguments().getLong("RESERVATION_ID", 0);
            loadReservationDetails(reservationId);
        }

        textEvent.setOnClickListener(v -> {
        });

        textService.setOnClickListener(v -> {
        });

        buttonCancel.setOnClickListener(v -> showCancelConfirmationDialog());

        return view;
    }

    private void loadReservationDetails(Long reservationId) {
        String auth = ClientUtils.getAuthorization(requireContext());
        if (auth == null) {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        ClientUtils.serviceReservationService.getReservationDetails(auth, reservationId)
                .enqueue(new Callback<GetServiceReservationDTO>() {
                    @Override
                    public void onResponse(@NonNull Call<GetServiceReservationDTO> call, @NonNull Response<GetServiceReservationDTO> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            populateFields(response.body());
                        } else {
                            Toast.makeText(getContext(), "Failed to load reservation details", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<GetServiceReservationDTO> call, @NonNull Throwable t) {
                        Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void populateFields(GetServiceReservationDTO dto) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        textEvent.setText(dto.getEventName());
        textService.setText(dto.getServiceName());
        textDate.setText(dto.getServiceDate() != null ? dto.getServiceDate() : "");
        textReservationMadeOn.setText(dto.getRequestDateTime() != null ? dto.getRequestDateTime().format(dateTimeFormatter) : "");
        textCancellationDeadline.setText(dto.getCancellationDeadline() != null ? dto.getCancellationDeadline() + " days" : "");
        textFinalAmount.setText(dto.getAmount() != null ? dto.getAmount() + " USD" : "");
        textTimeFrom.setText(dto.getTimeFrom() != null ? dto.getTimeFrom().format(timeFormatter) : "");
        textTimeTo.setText(dto.getTimeTo() != null ? dto.getTimeTo().format(timeFormatter) : "");
        textStatus.setText(dto.getStatus());

        if ("confirmed".equalsIgnoreCase(dto.getStatus())) {
            buttonCancel.setVisibility(View.VISIBLE);
        } else {
            buttonCancel.setVisibility(View.GONE);
        }
    }

    private void showCancelConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Cancel Reservation")
                .setMessage("Are you sure you want to cancel this reservation?")
                .setPositiveButton("Yes", (dialog, which) -> cancelReservation())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

    private void cancelReservation() {
        String auth = ClientUtils.getAuthorization(requireContext());
        if (auth == null) {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        ClientUtils.serviceReservationService.cancelReservation(auth, reservationId)
                .enqueue(new Callback<Map<String, String>>() {
                    @Override
                    public void onResponse(@NonNull Call<Map<String, String>> call, @NonNull Response<Map<String, String>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            String message = response.body().getOrDefault("message", "Reservation cancelled successfully.");
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            loadReservationDetails(reservationId);
                        } else {
                            String errorMessage = "Unknown error";
                            try {
                                if (response.errorBody() != null) {
                                    String errorBody = response.errorBody().string();
                                    JSONObject json = new JSONObject(errorBody);
                                    errorMessage = json.optString("message", errorBody);
                                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                errorMessage = "Failed to cancel reservation: " + e.getMessage();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Map<String, String>> call, @NonNull Throwable t) {
                        Toast.makeText(getContext(), "Error cancelling reservation: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

}
