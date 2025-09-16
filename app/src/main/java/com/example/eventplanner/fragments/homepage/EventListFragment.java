package com.example.eventplanner.fragments.homepage;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.activities.homepage.HomepageService;
import com.example.eventplanner.adapters.event.EventListAdapter;
import com.example.eventplanner.dto.event.GetEventDTO;
import com.example.eventplanner.enumeration.UserRole;
import com.example.eventplanner.utils.ClientUtils;
import com.example.eventplanner.viewmodels.EventFilterViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ImageButton prevPageButton, nextPageButton;
    private LinearLayout paginationIndicators;

    private EventFilterViewModel filterViewModel;

    private EventListAdapter adapter;
    private HomepageService service;

    private ChipGroup chipGroup;


    private boolean onlyFromMyCityActive = true;

    private final List<GetEventDTO> allEvents = new ArrayList<>();
    private int currentPage = 0;
    private final int pageSize = 2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);
        filterViewModel = new ViewModelProvider(requireActivity()).get(EventFilterViewModel.class);

        recyclerView = view.findViewById(R.id.eventRecyclerView);
        prevPageButton = view.findViewById(R.id.prevPageButton);
        nextPageButton = view.findViewById(R.id.nextPageButton);
        paginationIndicators = view.findViewById(R.id.paginationIndicators);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EventListAdapter(requireContext());
        recyclerView.setAdapter(adapter);

        service = ClientUtils.retrofit.create(HomepageService.class);

        loadAllEventsFromBackend();

        setupPaginationButtons();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialButton onlyFromMyCityBtn = view.findViewById(R.id.onlyFromMyCityButton);

        SharedPreferences sp = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String role = sp.getString("userRole", UserRole.ROLE_UNREGISTERED_USER.toString());

        filterViewModel = new ViewModelProvider(requireActivity()).get(EventFilterViewModel.class);
        chipGroup = view.findViewById(R.id.chipGroup);

        boolean isPrivileged = role.equals(UserRole.ROLE_ORGANIZER.toString()) ||
                role.equals(UserRole.ROLE_PROVIDER.toString());

        if (isPrivileged) {
            onlyFromMyCityBtn.setVisibility(View.VISIBLE);

            onlyFromMyCityActive = true;
            filterViewModel.setIgnoreCityFilter(false);
            updateButtonAppearance(onlyFromMyCityBtn, true);

            onlyFromMyCityBtn.setOnClickListener(v -> {
                // toggle
                onlyFromMyCityActive = !onlyFromMyCityActive;

                filterViewModel.setIgnoreCityFilter(!onlyFromMyCityActive);

                v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction(() -> {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                }).start();

                updateButtonAppearance(onlyFromMyCityBtn, onlyFromMyCityActive);

                filterViewModel.applyNow();
            });
        } else {
            onlyFromMyCityBtn.setVisibility(View.GONE);

        }

        // CHIPS observer
        filterViewModel.getAppliedFilters().observe(getViewLifecycleOwner(), this::updateChips);

        service = ClientUtils.retrofit.create(HomepageService.class);

        Button resetFiltersButton = view.findViewById(R.id.resetFiltersButton);
        resetFiltersButton.setOnClickListener(v -> {
            if (filterViewModel != null) filterViewModel.clearAllFilters();
        });

        Button filterButton = view.findViewById(R.id.filterButton);
        filterButton.setOnClickListener(v -> new EventFilterFragment()
                .show(getParentFragmentManager(), "EventFilterDialog"));

        filterViewModel.getAppliedFilters().observe(getViewLifecycleOwner(), payload -> {
            Log.d("EventListFragment", "Observer payload: " + payload);

            String token = sp.getString("token", null);
            String bearer = token != null ? "Bearer " + token : null;

            String cityParam = payload.getCities().isEmpty() ? null : payload.getCities().get(0);
            String typeParam = payload.getEventTypes().isEmpty() ? null : payload.getEventTypes().get(0);
            String startDateParam = formatDate(payload.getStartDate());
            String endDateParam = formatDate(payload.getEndDate());
            Integer ratingParam = payload.getRating();

            boolean ignoreCityParam = !isPrivileged || payload.isIgnoreCityFilter();



            Log.d("EventListFragment", "ignoreCityFilter to API: " + ignoreCityParam + " (privileged=" + isPrivileged + ")");

            service.searchEvents(
                    bearer,
                    null, // name
                    null, // description
                    typeParam,
                    null, // maxGuests
                    null, // country
                    null, // address
                    cityParam,
                    startDateParam,
                    endDateParam,
                    null, // attendance
                    ratingParam,
                    payload.getSortBy(),
                    payload.getSortDir(),
                    0,
                    100,
                    false,
                    ignoreCityParam
            ).enqueue(new Callback<List<GetEventDTO>>() {
                @Override
                public void onResponse(Call<List<GetEventDTO>> call, Response<List<GetEventDTO>> response) {
                    if (!isAdded()) return;
                    if (response.isSuccessful() && response.body() != null) {
                        allEvents.clear();
                        allEvents.addAll(response.body());
                        currentPage = 0;
                        updateRecyclerView();
                        Log.d("EventListFragment", "Events: " + response.body().size());
                    } else {
                        Log.d("EventListFragment", "Empty/error: " + response.code());
                        Toast.makeText(requireContext(), "No events to show.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<GetEventDTO>> call, Throwable t) {
                    if (!isAdded()) return;
                    Toast.makeText(getContext(), "Greška: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        filterViewModel.applyNow();
    }

    private void loadAllEventsFromBackend() {
        SharedPreferences sp = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String token = sp.getString("token", null);
        String bearer = token != null ? "Bearer " + token : null;

        String role = sp.getString("userRole", UserRole.ROLE_UNREGISTERED_USER.toString());
        boolean isPrivileged = role.equals(UserRole.ROLE_ORGANIZER.toString()) ||
                role.equals(UserRole.ROLE_PROVIDER.toString());

        Boolean ignoreCityParam;
        if (isPrivileged) {
            Boolean vmVal = filterViewModel.getIgnoreCityFilter().getValue();
            ignoreCityParam = vmVal != null ? vmVal : false;
        } else {
            ignoreCityParam = true;
        }


        Call<List<GetEventDTO>> call;

        if (token != null && !token.isEmpty()) {

            call = service.searchEvents(bearer,
                    null, null, null, null, null, null, null,
                    null, null, null, null,
                    "date", "asc", 0, 1000, false, ignoreCityParam
            );
        } else {

            call = service.searchEvents(bearer,
                    null, null, null, null, null, null, null,
                    null, null, null, null,
                    "date", "asc", 0, 1000, false, true
            );
        }

        call.enqueue(new Callback<List<GetEventDTO>>() {
            @Override
            public void onResponse(Call<List<GetEventDTO>> call, Response<List<GetEventDTO>> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    allEvents.clear();
                    allEvents.addAll(response.body());
                    currentPage = 0;
                    updateRecyclerView();
                } else {
                    Toast.makeText(requireContext(), "No events to show.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<GetEventDTO>> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setupPaginationButtons() {
        prevPageButton.setOnClickListener(v -> {
            if (currentPage > 0) {
                currentPage--;
                updateRecyclerView();
            }
        });

        nextPageButton.setOnClickListener(v -> {
            if ((currentPage + 1) * pageSize < allEvents.size()) {
                currentPage++;
                updateRecyclerView();
            }
        });
    }

    private void updateRecyclerView() {
        int totalPages = (int) Math.ceil((double) allEvents.size() / pageSize);
        int start = currentPage * pageSize;
        int end = Math.min(start + pageSize, allEvents.size());
        adapter.updateData(allEvents.subList(start, end));
        updatePaginationIndicators(totalPages);
    }

    private void updatePaginationIndicators(int totalPages) {
        if (getView() == null) return;
        paginationIndicators.removeAllViews();

        TextView pageInfo = new TextView(getContext());
        pageInfo.setText((currentPage + 1) + " / " + totalPages);
        pageInfo.setTextSize(14);
        pageInfo.setTextColor(Color.DKGRAY);
        pageInfo.setGravity(Gravity.CENTER);

        paginationIndicators.addView(pageInfo);
    }

    private void reloadEventsWithOnlyMyCity(boolean onlyMyCity) {
        if (filterViewModel == null || service == null) return;

        EventFilterViewModel.FilterPayload payload = filterViewModel.getAppliedFilters().getValue();
        if (payload == null) return;

        SharedPreferences sp = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String token = sp.getString("token", null);
        String bearer = token != null ? "Bearer " + token : null;

        String cityParam = payload.getCities().isEmpty() ? null : payload.getCities().get(0);
        String typeParam = payload.getEventTypes().isEmpty() ? null : payload.getEventTypes().get(0);
        String startDateParam = emptyToNull(payload.getStartDate());
        String endDateParam = emptyToNull(payload.getEndDate());
        Integer ratingParam = payload.getRating();

        service.searchEvents(
                bearer,
                null, // name
                null, // description
                typeParam,
                null, // maxGuests
                null, // country
                null, // address
                cityParam,
                startDateParam,
                endDateParam,
                null, // attendance
                ratingParam,
                payload.getSortBy(),
                payload.getSortDir(),
                0,
                100,
                false,
                !onlyMyCity
        ).enqueue(new Callback<List<GetEventDTO>>() {
            @Override
            public void onResponse(Call<List<GetEventDTO>> call, Response<List<GetEventDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allEvents.clear();
                    allEvents.addAll(response.body());
                    currentPage = 0;
                    updateRecyclerView();
                    Log.d("EventListFragment", "Backend returned " + response.body().size() + " events");
                } else {
                    Log.d("EventListFragment", "Backend returned empty or error: " + response.code());
                    Toast.makeText(requireContext(), "No events to show.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<GetEventDTO>> call, Throwable t) {
                Toast.makeText(getContext(), "Greška: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addCircle(int page, boolean isCurrent) {
        TextView circle = new TextView(getContext());
        circle.setText(String.valueOf(page + 1));
        circle.setTextSize(12);
        circle.setTextColor(isCurrent ? Color.WHITE : Color.GRAY);
        circle.setBackgroundResource(R.drawable.circle_indicator);
        circle.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(8, 0, 8, 0);
        circle.setLayoutParams(params);

        paginationIndicators.addView(circle);
    }



    private void updateChips(EventFilterViewModel.FilterPayload p) {
        chipGroup.removeAllViews();

        for (String c : p.cities) addFilterChip("City: " + c, () -> {
            filterViewModel.removeCity(c);
            filterViewModel.applyNow();
        });
        for (String t : p.eventTypes) addFilterChip("Type: " + t, () -> {
            filterViewModel.removeEventType(t);
            filterViewModel.applyNow();
        });
        if (p.rating != null) addFilterChip("Rating: " + p.rating, () -> {
            filterViewModel.setSelectedRating(null);
            filterViewModel.applyNow();
        });
        if (p.sortBy != null && !p.sortBy.isEmpty()) addFilterChip("Sort: " + p.sortBy, () -> {
            filterViewModel.setSelectedSortOptions(null);
            filterViewModel.applyNow();
        });
        if (p.sortDir != null && !p.sortDir.isEmpty()) addFilterChip("Dir: " + p.sortDir, () -> {
            filterViewModel.setSortDir(null);
            filterViewModel.applyNow();
        });
        if (p.startDate != null && !p.startDate.isEmpty()) addFilterChip("From: " + p.startDate, () -> {
            filterViewModel.setMinDate("");
            filterViewModel.applyNow();
        });
        if (p.endDate != null && !p.endDate.isEmpty()) addFilterChip("To: " + p.endDate, () -> {
            filterViewModel.setMaxDate("");
            filterViewModel.applyNow();
        });
    }

    private void addFilterChip(String text, Runnable onRemove) {
        Chip chip = new Chip(getContext());
        chip.setText(text);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> {
            chipGroup.removeView(chip);
            onRemove.run();
        });
        chipGroup.addView(chip);
    }

    private String emptyToNull(String s) {
        return (s == null || s.isEmpty()) ? null : s;
    }


    private void updateButtonAppearance(MaterialButton button, boolean isActive) {
        int strokeColor = isActive
                ? ContextCompat.getColor(requireContext(), R.color.white)
                : ContextCompat.getColor(requireContext(), R.color.light_gray);

        int textColor = isActive
                ? ContextCompat.getColor(requireContext(), R.color.dark_gray)
                : ContextCompat.getColor(requireContext(), R.color.black);

        button.animate()
                .setDuration(200)
                .withStartAction(() -> {
                    button.setStrokeColor(ColorStateList.valueOf(strokeColor));
                    button.setTextColor(textColor);
                })
                .start();

        int bgColor = isActive
                ? ContextCompat.getColor(requireContext(), R.color.activeButtonBackground)
                : ContextCompat.getColor(requireContext(), R.color.inactiveButtonBackground);
        button.setBackgroundColor(bgColor);
    }

    private String formatDate(String rawDate) {
        if (rawDate == null || rawDate.isEmpty()) return null;
        try {
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat output = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            return output.format(input.parse(rawDate));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }



}

