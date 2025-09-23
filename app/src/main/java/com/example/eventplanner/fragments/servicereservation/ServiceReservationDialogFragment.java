package com.example.eventplanner.fragments.servicereservation;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.eventplanner.R;
import com.example.eventplanner.dto.event.GetEventDTO;
import com.example.eventplanner.dto.servicereservation.CreateServiceReservationDTO;
import com.example.eventplanner.dto.servicereservation.CreatedServiceReservationDTO;
import com.example.eventplanner.utils.ClientUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceReservationDialogFragment extends DialogFragment {

    private Spinner spinnerEvent;
    private EditText editTextDate, editTextTimeFrom, editTextTimeTo;
    private Button buttonReserve;

    private List<Event> organizerEvents;
    private long serviceId;
    private long minDurationMinutes = 0;
    private long maxDurationMinutes = 0;
    private long fixedDurationMinutes = 0;

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_service_reservation_dialog, container, false);

        if (getArguments() != null) {
            serviceId = getArguments().getLong("SERVICE_ID", 0);
            fixedDurationMinutes = getArguments().getLong("FIXED_DURATION", 0);
            minDurationMinutes = getArguments().getLong("MIN_DURATION", 0);
            maxDurationMinutes = getArguments().getLong("MAX_DURATION", 0);
            Log.d("ServiceReservationDialog", "Durations in minutes: fixed=" + fixedDurationMinutes +
                    ", min=" + minDurationMinutes + ", max=" + maxDurationMinutes);

        }

        spinnerEvent = view.findViewById(R.id.spinnerEvent);
        editTextDate = view.findViewById(R.id.editTextDate);
        editTextTimeFrom = view.findViewById(R.id.editTextTimeFrom);
        editTextTimeTo = view.findViewById(R.id.editTextTimeTo);
        buttonReserve = view.findViewById(R.id.buttonReserve);

        setupEventSpinner();
        setupDatePicker();
        setupTimePickers();

        buttonReserve.setOnClickListener(v -> reserveService());

        return view;
    }

    private void setupEventSpinner() {

        String auth = ClientUtils.getAuthorization(requireContext());
        if (auth == null) {
            Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }
        ClientUtils.eventService.getEventsByOrganizer(auth).enqueue(new retrofit2.Callback<List<GetEventDTO>>() {
            @Override
            public void onResponse(Call<List<GetEventDTO>> call, Response<List<GetEventDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<GetEventDTO> events = response.body();
                    if (events.isEmpty()) {
                        spinnerEvent.setEnabled(false);
                        buttonReserve.setEnabled(false);
                        Toast.makeText(getContext(), "You do not have events to create a reservation", Toast.LENGTH_SHORT).show();
                    } else {
                        List<String> eventNames = new ArrayList<>();
                        for (GetEventDTO event : events) {
                            eventNames.add(event.getName());
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                                android.R.layout.simple_spinner_item, eventNames);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerEvent.setAdapter(adapter);

                        spinnerEvent.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                                GetEventDTO selectedEvent = events.get(position);
                                editTextDate.setText(selectedEvent.getDate().toString());
                            }

                            @Override
                            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
                        });
                    }
                } else {
                    spinnerEvent.setEnabled(false);
                    buttonReserve.setEnabled(false);
                    Toast.makeText(getContext(), "Failed to load events", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<GetEventDTO>> call, Throwable t) {
                spinnerEvent.setEnabled(false);
                buttonReserve.setEnabled(false);
                Toast.makeText(getContext(), "Error loading events: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupDatePicker() {
        editTextDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePicker = new DatePickerDialog(requireContext(), (view, selectedYear, selectedMonth, selectedDay) -> {
                editTextDate.setText(String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear));
            }, year, month, day);

            datePicker.getDatePicker().setMinDate(calendar.getTimeInMillis());
            datePicker.show();
        });
    }

    private void setupTimePickers() {
        editTextTimeFrom.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePicker = new TimePickerDialog(requireContext(), (view, selectedHour, selectedMinute) -> {
                LocalTime fromTime = LocalTime.of(selectedHour, selectedMinute);
                editTextTimeFrom.setText(fromTime.format(DateTimeFormatter.ofPattern("HH:mm")));

                if (fixedDurationMinutes > 0) {
                    LocalTime toTime = fromTime.plusMinutes(fixedDurationMinutes);
                    editTextTimeTo.setText(toTime.format(DateTimeFormatter.ofPattern("HH:mm")));
                }
            }, hour, minute, true);

            timePicker.show();
        });

        if (fixedDurationMinutes > 0) {
            // Fixed duration → disable end time
            editTextTimeTo.setEnabled(false);
        } else {
            // Min/Max duration → enable end time
            editTextTimeTo.setEnabled(true);
            editTextTimeTo.setOnClickListener(v -> {
                String fromText = editTextTimeFrom.getText().toString();
                if (fromText.isEmpty()) {
                    Toast.makeText(requireContext(), "Please select start time first", Toast.LENGTH_SHORT).show();
                    return;
                }

                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePicker = new TimePickerDialog(requireContext(), (view, selectedHour, selectedMinute) -> {
                    LocalTime toTime = LocalTime.of(selectedHour, selectedMinute);
                    LocalTime fromTime = LocalTime.parse(fromText, DateTimeFormatter.ofPattern("HH:mm"));
                    long durationMinutes = java.time.Duration.between(fromTime, toTime).toMinutes();

                    if (durationMinutes < minDurationMinutes || durationMinutes > maxDurationMinutes) {
                        Toast.makeText(requireContext(),
                                "Service duration must be between " + minDurationMinutes/60 + " and " + maxDurationMinutes/60 + " hours",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    editTextTimeTo.setText(toTime.format(DateTimeFormatter.ofPattern("HH:mm")));
                }, hour, minute, true);

                timePicker.show();
            });
        }
        }


    private void reserveService() {
        if (spinnerEvent.getSelectedItem() == null || editTextDate.getText().toString().isEmpty() ||
                editTextTimeFrom.getText().toString().isEmpty() || editTextTimeTo.getText().toString().isEmpty()) {
            Toast.makeText(requireContext(), "Fill out all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        GetEventDTO selectedEvent = (GetEventDTO) spinnerEvent.getSelectedItem();
        CreateServiceReservationDTO dto = new CreateServiceReservationDTO();
        dto.setServiceId(serviceId);
        dto.setEventId(selectedEvent.getId());
        dto.setRequestedTimeFrom(LocalTime.parse(editTextTimeFrom.getText().toString(), DateTimeFormatter.ofPattern("HH:mm")));
        dto.setRequestedTimeTo(LocalTime.parse(editTextTimeTo.getText().toString(), DateTimeFormatter.ofPattern("HH:mm")));

        String authorization = ClientUtils.getAuthorization(getContext());
        ClientUtils.serviceReservationService.reserveService(authorization, serviceId, dto)
                .enqueue(new Callback<CreatedServiceReservationDTO>() {
                    @Override
                    public void onResponse(Call<CreatedServiceReservationDTO> call, Response<CreatedServiceReservationDTO> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(requireContext(), "Service booked!", Toast.LENGTH_SHORT).show();
                            dismiss();
                        } else {
                            Toast.makeText(requireContext(), "Error reserving the service", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<CreatedServiceReservationDTO> call, Throwable t) {
                        Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static class Event {
        private Long id;
        private String name;
        private LocalDate date;

        public Event(Long id, String name, LocalDate date) {
            this.id = id;
            this.name = name;
            this.date = date;
        }

        public Long getId() { return id; }
        public String getName() { return name; }
        public LocalDate getDate() { return date; }

        @NonNull
        @Override
        public String toString() { return name; }
    }
}
