    package com.example.eventplanner.fragments.homepage;

    import android.content.Context;
    import android.content.SharedPreferences;
    import android.util.Log;
    import android.view.View;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.lifecycle.ViewModelProvider;
    import androidx.recyclerview.widget.RecyclerView;

    import com.example.eventplanner.R;
    import com.example.eventplanner.activities.homepage.HomepageService;
    import com.example.eventplanner.adapters.event.EventListAdapter;
    import com.example.eventplanner.adapters.homepage.ListItemAdapter;
    import com.example.eventplanner.dto.event.GetEventDTO;
    import com.example.eventplanner.enumeration.UserRole;
    import com.example.eventplanner.utils.ClientUtils;
    import com.example.eventplanner.viewmodels.EventFilterViewModel;
    import com.google.android.material.chip.Chip;

    import java.text.ParseException;
    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Locale;

    import retrofit2.Call;
    import retrofit2.Callback;
    import retrofit2.Response;

    public class EventListFragment extends BaseListFragment<GetEventDTO, EventFilterViewModel> {

        private HomepageService service;
        private boolean isPrivileged;

        @Override
        protected int getLayoutResId() {
            return R.layout.fragment_base_list;
        }

        @Override
        protected void setupViewModel() {
            filterViewModel = new ViewModelProvider(this).get(EventFilterViewModel.class);
            service = ClientUtils.retrofit.create(HomepageService.class);

            SharedPreferences sp = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
            String role = sp.getString("userRole", UserRole.ROLE_UNREGISTERED_USER.toString());
            isPrivileged = role.equals(UserRole.ROLE_ORGANIZER.toString()) || role.equals(UserRole.ROLE_PROVIDER.toString());

            if (onlyFromMyCityBtn != null) {
                onlyFromMyCityBtn.setVisibility(isPrivileged ? View.VISIBLE : View.GONE);
                if (isPrivileged) {
                    filterViewModel.getIgnoreCityFilter().observe(getViewLifecycleOwner(), ignoreCity -> {
                        updateButtonAppearance(onlyFromMyCityBtn, !ignoreCity);
                    });

                    onlyFromMyCityBtn.setOnClickListener(v -> {
                        boolean currentIgnoreState = Boolean.TRUE.equals(filterViewModel.getIgnoreCityFilter().getValue());
                        filterViewModel.setIgnoreCityFilter(!currentIgnoreState);
                        filterViewModel.applyNow();
                    });
                }
            }

            filterViewModel.getAppliedFilters().observe(getViewLifecycleOwner(), payload -> {
                Log.d("EventListFragment", "Observer payload: " + payload);
                loadItemsFromBackend(payload);
                updateChips(payload);
            });

            filterViewModel.applyNow();
        }

        @Override
        protected RecyclerView.Adapter<?> createAdapter() {
            return new ListItemAdapter(requireContext());
        }

        @Override
        protected void loadItemsFromBackend(Object payloadObj) {
            EventFilterViewModel.FilterPayload payload = (EventFilterViewModel.FilterPayload) payloadObj;
            if (payload == null) return;

            SharedPreferences sp = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
            String token = sp.getString("token", null);
            String bearer = token != null ? "Bearer " + token : null;

            String cityParam = payload.getCities().isEmpty() ? null : payload.getCities().get(0);
            String typeParam = payload.getEventTypes().isEmpty() ? null : payload.getEventTypes().get(0);
            String startDateParam = formatDate(payload.getStartDate());
            String endDateParam = formatDate(payload.getEndDate());
            Integer ratingParam = payload.getRating();
            boolean ignoreCityParam = !isPrivileged || payload.isIgnoreCityFilter();
            String searchQueryParam = (payload.getSearchQuery() != null && !payload.getSearchQuery().isEmpty()) ? payload.getSearchQuery() : null;

            service.searchEvents(
                    bearer,
                    searchQueryParam,
                    null,
                    typeParam,
                    null, null, null,
                    cityParam,
                    startDateParam,
                    endDateParam,
                    null,
                    ratingParam,
                    payload.getSortBy(),
                    payload.getSortDir(),
                    0,
                    100,
                    false,
                    ignoreCityParam
            ).enqueue(new Callback<List<GetEventDTO>>() {
                @Override
                public void onResponse(@NonNull Call<List<GetEventDTO>> call, @NonNull Response<List<GetEventDTO>> response) {
                    if (!isAdded()) return;
                    if (response.isSuccessful() && response.body() != null) {
                        allItems.clear();
                        allItems.addAll(response.body());
                        currentPage = 0;
                        updateRecyclerView();
                    } else {
                        Toast.makeText(requireContext(), "No events to show.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<GetEventDTO>> call, @NonNull Throwable t) {
                    if (!isAdded()) return;
                    Toast.makeText(requireContext(), "Greška: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        protected void showFilterDialog() {
            new EventFilterFragment().show(getChildFragmentManager(), "EventFilterDialog");
        }

        @Override
        protected void addSearchChip(String query) {
            if (query == null || query.isEmpty()) return;

            for (int i = 0; i < chipGroup.getChildCount(); i++) {
                Chip chip = (Chip) chipGroup.getChildAt(i);
                if (chip.getText().toString().equals("Search: " + query)) {
                    return;
                }
            }

            filterViewModel.setSearchQuery(query);
            filterViewModel.applyNow();
        }


        protected void updateChips(EventFilterViewModel.FilterPayload p) {
            chipGroup.removeAllViews();
            if (p.getSearchQuery() != null && !p.getSearchQuery().isEmpty()) {
                addFilterChip("Search: " + p.getSearchQuery(), () -> {
                    filterViewModel.setSearchQuery(null);
                    filterViewModel.applyNow();
                });
            }
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