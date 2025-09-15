package com.example.eventplanner.fragments.servicecreation;

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

import com.example.eventplanner.R;

import java.util.Calendar;

public class ServiceCreation5 extends Fragment {

    private Button selectDateButton;
    private Button selectFromTimeButton;
    private Button selectToTimeButton;
    private Button submitButton;
    private CheckBox monCheckBox, tueCheckBox, wedCheckBox, thuCheckBox, friCheckBox, satCheckBox, sunCheckBox;

    public ServiceCreation5() {
        // Required empty public constructor
    }

    public static ServiceCreation5 newInstance() {
        return new ServiceCreation5();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service_creation5, container, false);

        // Inicijalizacija View-a
        selectDateButton = view.findViewById(R.id.select_date_button);
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

        selectFromTimeButton.setOnClickListener(v -> showTimePicker(selectFromTimeButton));

        selectToTimeButton.setOnClickListener(v -> showTimePicker(selectToTimeButton));

        submitButton.setOnClickListener(v -> {
            if (validateForm()) {
                Toast.makeText(getContext(), "Podaci uspešno validirani i poslati!", Toast.LENGTH_SHORT).show();
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
                    selectDateButton.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void showTimePicker(final Button button) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                (view, hourOfDay, minuteOfHour) -> {
                    String time = String.format("%02d:%02d", hourOfDay, minuteOfHour);
                    button.setText(time);
                }, hour, minute, true); // true za 24-satni format
        timePickerDialog.show();
    }

    private boolean validateForm() {
//        if (selectDateButton.getText().toString().equals(getString(R.string.select_date))) {
//            // Ako datum nije odabran, ne smatramo to greškom, jer se može preskočiti
//        }

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
            String[] fromParts = fromTime.split(":");
            int fromMinutes = Integer.parseInt(fromParts[0]) * 60 + Integer.parseInt(fromParts[1]);

            String[] toParts = toTime.split(":");
            int toMinutes = Integer.parseInt(toParts[0]) * 60 + Integer.parseInt(toParts[1]);

            if (fromMinutes >= toMinutes) {
                Toast.makeText(getContext(), "Početno vreme mora biti pre krajnjeg.", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Greška pri parsiranju vremena.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}