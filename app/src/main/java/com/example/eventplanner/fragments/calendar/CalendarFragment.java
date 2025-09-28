package com.example.eventplanner.fragments.calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.fragments.event.EventDetailsFragment;
import com.example.eventplanner.utils.ClientUtils;
import com.example.eventplanner.R;
import com.example.eventplanner.enumeration.UserRole;
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

public class CalendarFragment extends Fragment {

    private RecyclerView calendarRecyclerView;
    private CalendarAdapter calendarAdapter;
    private TextView monthYearText;
    private Calendar currentCalendar;
    private HashMap<String, List<String>> acceptedEvents = new HashMap<>();
    private HashMap<String, List<String>> createdEvents = new HashMap<>();
    private View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_calendar, container, false);

        monthYearText = view.findViewById(R.id.monthYearText);
        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);
        Button previousMonthButton = view.findViewById(R.id.previousMonthButton);
        Button nextMonthButton = view.findViewById(R.id.nextMonthButton);

        currentCalendar = Calendar.getInstance();

        calendarRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 7));
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

        return view;

    }





    private void updateCalendar() {
        int month = currentCalendar.get(Calendar.MONTH);
        int year = currentCalendar.get(Calendar.YEAR);
        monthYearText.setText(getMonthName(month) + " " + year);

        List<String> days = generateDaysForMonth();

        // all users can see accepted events in calendar
        loadAcceptedEvents(acceptedEvents);

        // only organizers can see events they created too
        SharedPreferences pref = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String role = pref.getString("userRole", UserRole.ROLE_ORGANIZER.toString());

        if (role.equals(UserRole.ROLE_ORGANIZER.toString())) {
            loadCreatedEvents(createdEvents);
        }

        // Update adapter
        if (calendarAdapter == null) {
            calendarAdapter = new CalendarAdapter(days, acceptedEvents, createdEvents, month, year,
                eventId -> {
                    Fragment detailsFragment = EventDetailsFragment.newInstance(eventId);
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_fragment_container, detailsFragment)
                            .addToBackStack(null)
                            .commit();
                });
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



    private void loadAcceptedEvents(HashMap<String, List<String>> events) {
        String auth = ClientUtils.getAuthorization(requireContext());

        SharedPreferences pref = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
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

                        if (!events.containsKey(formattedDate)) {
                            events.put(formattedDate, new ArrayList<>());
                        }
                        events.get(formattedDate).add(event.getName());
                    }

                    requireActivity().runOnUiThread(() -> {
                        calendarAdapter.updateData(generateDaysForMonth(), events, createdEvents, currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.YEAR));
                        calendarAdapter.notifyDataSetChanged();
                    });
                }
            }

            @Override
            public void onFailure(Call<ArrayList<AcceptedEventDTO>> call, Throwable t) {
                Toast.makeText(requireActivity(), "Failed to load accepted events!", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void loadCreatedEvents(HashMap<String, List<String>> events) {
        String auth = ClientUtils.getAuthorization(requireContext());

        SharedPreferences pref = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
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

                        if (!events.containsKey(formattedDate)) {
                            events.put(formattedDate, new ArrayList<>());
                        }
                        events.get(formattedDate).add(event.getName());
                    }

                    requireActivity().runOnUiThread(() -> {
                        calendarAdapter.updateData(generateDaysForMonth(), acceptedEvents, events, currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.YEAR));
                        calendarAdapter.notifyDataSetChanged();
                    });
                } else if (response.code() == 404) {
                    Toast.makeText(requireActivity(), "You don't have any events!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<AcceptedEventDTO>> call, Throwable t) {
                Toast.makeText(requireActivity(), "Failed to load created events!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
