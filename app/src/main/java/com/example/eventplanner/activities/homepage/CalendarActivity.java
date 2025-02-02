package com.example.eventplanner.activities.homepage;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.CalendarAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class CalendarActivity extends AppCompatActivity {

    private RecyclerView calendarRecyclerView;
    private CalendarAdapter calendarAdapter;
    private TextView monthYearText;
    private Calendar currentCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        monthYearText = findViewById(R.id.monthYearText);
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        Button previousMonthButton = findViewById(R.id.previousMonthButton);
        Button nextMonthButton = findViewById(R.id.nextMonthButton);

        currentCalendar = Calendar.getInstance();

        calendarRecyclerView.setLayoutManager(new GridLayoutManager(this, 7));
        calendarRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = 8;
                outRect.bottom = 8;
            }
        });

        // Set the adapter for the calendar
        updateCalendar();

        // Set button listeners for month navigation
        previousMonthButton.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, -1);
            updateCalendar();
        });

        nextMonthButton.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, 1);
            updateCalendar();
        });
    }

    private void updateCalendar() {
        // Update the month/year text
        int month = currentCalendar.get(Calendar.MONTH);
        int year = currentCalendar.get(Calendar.YEAR);
        monthYearText.setText(getMonthName(month) + " " + year);

        // Generate the days for the current month and update the adapter
        List<String> days = generateDaysForMonth();
        HashMap<Integer, String> events = new HashMap<>();
        events.put(13, "Wedding Celebration");
        events.put(17, "Graduation Hackathon");
        events.put(15, "Baby Shower for Emma");

        if (calendarAdapter == null) {
            calendarAdapter = new CalendarAdapter(days, events);
            calendarRecyclerView.setAdapter(calendarAdapter);
        } else {
            calendarAdapter.updateData(days, events);  // Update the existing adapter data
        }
    }

    private List<String> generateDaysForMonth() {
        List<String> days = new ArrayList<>();
        int month = currentCalendar.get(Calendar.MONTH);
        int year = currentCalendar.get(Calendar.YEAR);

        // Set the first day of the month
        currentCalendar.set(year, month, 1);

        int firstDayOfWeek = (currentCalendar.get(Calendar.DAY_OF_WEEK) + 5) % 7;
        int daysInMonth = currentCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Add empty spaces for days before the first day
        for (int i = 0; i < firstDayOfWeek; i++) {
            days.add("");
        }

        // Add the actual days
        for (int i = 1; i <= daysInMonth; i++) {
            days.add(String.valueOf(i));
        }

        return days;
    }

    private String getMonthName(int month) {
        String[] months = {"January", "February", "March", "April", "May", "June", "July",
                "August", "September", "October", "November", "December"};
        return months[month];
    }


}
