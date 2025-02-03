package com.example.eventplanner.activities.calendar;

import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.ClientUtils;
import com.example.eventplanner.R;
import com.example.eventplanner.adapters.CalendarAdapter;
import com.example.eventplanner.dto.event.AcceptedEventDTO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalendarActivity extends AppCompatActivity {

    private RecyclerView calendarRecyclerView;
    private CalendarAdapter calendarAdapter;
    private TextView monthYearText;
    private Calendar currentCalendar;
    private HashMap<String, String> acceptedEvents = new HashMap<>();
    private HashMap<String, String> createdEvents = new HashMap<>();


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
        int month = currentCalendar.get(Calendar.MONTH);
        int year = currentCalendar.get(Calendar.YEAR);
        monthYearText.setText(getMonthName(month) + " " + year);

        List<String> days = generateDaysForMonth();

        loadAcceptedEvents(acceptedEvents);
        loadCreatedEvents(createdEvents);

        // Update adapter
        if (calendarAdapter == null) {
            calendarAdapter = new CalendarAdapter(days, acceptedEvents, createdEvents, month, year);
            calendarRecyclerView.setAdapter(calendarAdapter);
        } else {
            calendarAdapter.updateData(days, acceptedEvents, createdEvents, month, year);
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



    private void loadAcceptedEvents(HashMap<String, String> events) {
        String auth = ClientUtils.getAuthorization(this);

        SharedPreferences pref = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String email = pref.getString("email", "e");

        Call<ArrayList<AcceptedEventDTO>> call = ClientUtils.userService.getAcceptedEvents(auth, email);
        call.enqueue(new Callback<ArrayList<AcceptedEventDTO>>() {
            @Override
            public void onResponse(Call<ArrayList<AcceptedEventDTO>> call, Response<ArrayList<AcceptedEventDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    events.clear();

                    for (AcceptedEventDTO event : response.body()) {
                        Calendar eventCalendar = Calendar.getInstance();
                        eventCalendar.setTime(event.getDate());

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        String formattedDate = sdf.format(eventCalendar.getTime());

                        events.put(formattedDate, event.getName());
                    }

                    runOnUiThread(() -> {
                        calendarAdapter.updateData(generateDaysForMonth(), events, createdEvents, currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.YEAR));
                        calendarAdapter.notifyDataSetChanged();
                    });
                }
            }

            @Override
            public void onFailure(Call<ArrayList<AcceptedEventDTO>> call, Throwable t) {
                Toast.makeText(CalendarActivity.this, "Failed to load accepted events!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadCreatedEvents(HashMap<String, String> events) {
        String auth = ClientUtils.getAuthorization(this);

        SharedPreferences pref = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String email = pref.getString("email", "e");

        Call<ArrayList<AcceptedEventDTO>> call = ClientUtils.userService.getCreatedEvents(auth, email);
        call.enqueue(new Callback<ArrayList<AcceptedEventDTO>>() {
            @Override
            public void onResponse(Call<ArrayList<AcceptedEventDTO>> call, Response<ArrayList<AcceptedEventDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    events.clear();

                    for (AcceptedEventDTO event : response.body()) {
                        Calendar eventCalendar = Calendar.getInstance();
                        eventCalendar.setTime(event.getDate());

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        String formattedDate = sdf.format(eventCalendar.getTime());

                        events.put(formattedDate, event.getName());
                    }

                    runOnUiThread(() -> {
                        calendarAdapter.updateData(generateDaysForMonth(), acceptedEvents, events, currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.YEAR));
                        calendarAdapter.notifyDataSetChanged();
                    });
                }
            }

            @Override
            public void onFailure(Call<ArrayList<AcceptedEventDTO>> call, Throwable t) {
                Toast.makeText(CalendarActivity.this, "Failed to load accepted events!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void closeForm(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }
}
