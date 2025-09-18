package com.example.eventplanner.fragments.servicecreation;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventplanner.R;
import com.example.eventplanner.viewmodels.ServiceCreationViewModel;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ServiceCreation5 extends Fragment {

    private Button selectDateButton;
    private Button showDatesButton;
    private Button selectFromTimeButton;
    private Button selectToTimeButton;
    private Button submitButton;
    private CheckBox monCheckBox, tueCheckBox, wedCheckBox, thuCheckBox, friCheckBox, satCheckBox, sunCheckBox;
    private ServiceCreationViewModel viewModel;

    public ServiceCreation5() {
        // Required empty public constructor
    }

    public static ServiceCreation5 newInstance() {
        return new ServiceCreation5();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ServiceCreationViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service_creation5, container, false);

        selectDateButton = view.findViewById(R.id.select_date_button);
        showDatesButton = view.findViewById(R.id.show_dates_button);
        selectFromTimeButton = view.findViewById(R.id.select_from_time_button);
        selectToTimeButton = view.findViewById(R.id.select_to_time_button);
        submitButton = view.findViewById(R.id.submitService);
        ImageView xButton = view.findViewById(R.id.imageView5);

        monCheckBox = view.findViewById(R.id.mon_checkbox);
        tueCheckBox = view.findViewById(R.id.tue_checkbox);
        wedCheckBox = view.findViewById(R.id.wed_checkbox);
        thuCheckBox = view.findViewById(R.id.thu_checkbox);
        friCheckBox = view.findViewById(R.id.fri_checkbox);
        satCheckBox = view.findViewById(R.id.sat_checkbox);
        sunCheckBox = view.findViewById(R.id.sun_checkbox);

        selectDateButton.setOnClickListener(v -> showDatePicker());
        showDatesButton.setOnClickListener(v -> showSelectedDatesDialog());

        selectFromTimeButton.setOnClickListener(v -> showTimePicker(selectFromTimeButton));

        selectToTimeButton.setOnClickListener(v -> showTimePicker(selectToTimeButton));

        submitButton.setOnClickListener(v -> {
            if (validateForm()) {
                viewModel.mapServiceDataAndCreateService();
//                if (getActivity() != null) {
//                    getActivity().getSupportFragmentManager().popBackStack();
//                }
            }
        });

        xButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    viewModel.addUnavailableDate(date);
                    Toast.makeText(getContext(), "Datum " + date + " dodat.", Toast.LENGTH_SHORT).show();
                }, year, month, day);
        datePickerDialog.show();
    }
    private void showSelectedDatesDialog() {
        List<String> dates = viewModel.getUnavailableDates().getValue();
        if (dates == null || dates.isEmpty()) {
            Toast.makeText(getContext(), "Niste odabrali nijedan datum.", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Odabrani datumi");

        final String[] datesArray = dates.toArray(new String[0]);

        builder.setItems(datesArray, (dialog, which) -> {
        });

        builder.setPositiveButton("Ukloni", (dialog, which) -> {
            showRemoveDateDialog(datesArray);
        });
        builder.setNegativeButton("Zatvori", null);

        builder.show();
    }

    private void showRemoveDateDialog(String[] dates) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Uklonite datum");
        builder.setItems(dates, (dialog, which) -> {
            String dateToRemove = dates[which];
            viewModel.removeUnavailableDate(dateToRemove);
            Toast.makeText(getContext(), "Datum " + dateToRemove + " uklonjen.", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Odustani", null);
        builder.show();
    }

    private void showTimePicker(final Button button) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                (view, hourOfDay, minuteOfHour) -> {
                    String time = String.format("%02d:%02d", hourOfDay, minuteOfHour);
                    button.setText(time);
                }, hour, minute, true);
        timePickerDialog.show();
    }
    private void sendDataToViewModel(LocalTime fromTime, LocalTime toTime) {
        if (viewModel == null) {
            return;
        }

        List<Integer> selectedDaysList = new ArrayList<>();
        if (monCheckBox.isChecked()) selectedDaysList.add(1);
        if (tueCheckBox.isChecked()) selectedDaysList.add(2);
        if (wedCheckBox.isChecked()) selectedDaysList.add(3);
        if (thuCheckBox.isChecked()) selectedDaysList.add(4);
        if (friCheckBox.isChecked()) selectedDaysList.add(5);
        if (satCheckBox.isChecked()) selectedDaysList.add(6);
        if (sunCheckBox.isChecked()) selectedDaysList.add(7);

        viewModel.setSelectedDays(selectedDaysList);
        viewModel.setSelectedFromTime(fromTime);
        viewModel.setSelectedToTime(toTime);

    }

    private boolean validateForm() {

        if (!monCheckBox.isChecked() && !tueCheckBox.isChecked() && !wedCheckBox.isChecked() &&
                !thuCheckBox.isChecked() && !friCheckBox.isChecked() && !satCheckBox.isChecked() &&
                !sunCheckBox.isChecked()) {
            Toast.makeText(getContext(), "Molimo odaberite bar jedan dostupan dan.", Toast.LENGTH_SHORT).show();
            return false;
        }

        String fromTime = selectFromTimeButton.getText().toString();
        String toTime = selectToTimeButton.getText().toString();

        if (fromTime.equals(getString(R.string.select_time)) || toTime.equals(getString(R.string.select_time))) {
            Toast.makeText(getContext(), "Morate odabrati početno i krajnje vreme.", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            LocalTime from = LocalTime.parse(fromTime);
            LocalTime to = LocalTime.parse(toTime);

            if (from.isAfter(to) || from.equals(to)) {
                Toast.makeText(getContext(), "Početno vreme mora biti pre krajnjeg.", Toast.LENGTH_SHORT).show();
                return false;
            }

            sendDataToViewModel(from, to);

        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Greška pri parsiranju vremena.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}