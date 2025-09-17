package com.example.eventplanner.fragments.homepage;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.activities.homepage.HomepageService;
import com.example.eventplanner.adapters.homepage.ListItemAdapter;
import com.example.eventplanner.dto.solution.GetHomepageSolutionDTO;
import com.example.eventplanner.enumeration.UserRole;
import com.example.eventplanner.fragments.product.SolutionFilterFragment;
import com.example.eventplanner.utils.ClientUtils;
import com.example.eventplanner.viewmodels.HomeSolutionFilterViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SolutionListFragment extends BaseListFragment<GetHomepageSolutionDTO, HomeSolutionFilterViewModel> {

    private HomepageService service;
    private boolean isPrivileged;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_base_list;
    }

    @Override
    protected void setupViewModel() {
        filterViewModel = new ViewModelProvider(this).get(HomeSolutionFilterViewModel.class);
        service = ClientUtils.retrofit.create(HomepageService.class);

        if (listTitle != null) {
            listTitle.setVisibility(View.VISIBLE);
            listTitle.setText(R.string.all_products_services);
        }
        if (filterButtonsLayout != null) filterButtonsLayout.setVisibility(View.VISIBLE);
        if (solutionTypeRadioGroup != null) solutionTypeRadioGroup.setVisibility(View.VISIBLE);

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

        if (solutionTypeRadioGroup != null) {
            solutionTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                String solutionType = null;
                if (checkedId == R.id.radioProducts) {
                    solutionType = "PRODUCT";
                } else if (checkedId == R.id.radioServices) {
                    solutionType = "SERVICE";
                } else {
                    solutionType = null;
                }
                filterViewModel.setType(solutionType);
                filterViewModel.applyNow();
            });
        }

        filterViewModel.getAppliedFilters().observe(getViewLifecycleOwner(), payload -> {
            if (solutionTypeRadioGroup != null) {
                if (payload.getType() == null) {
                    solutionTypeRadioGroup.check(R.id.radioAll);
                }
            }
            loadItemsFromBackend(payload);
            updateChips(payload);
        });

        filterViewModel.applyNow();
    }

    @Override
    protected RecyclerView.Adapter<?> createAdapter() {
        return new ListItemAdapter(getContext());
    }

    @Override
    protected void loadItemsFromBackend(Object payloadObj) {
        HomeSolutionFilterViewModel.FilterPayload payload = (HomeSolutionFilterViewModel.FilterPayload) payloadObj;
        if (payload == null) return;

        SharedPreferences sp = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String token = sp.getString("token", null);
        String bearer = token != null ? "Bearer " + token : null;

        service.searchSolutions(
                bearer,
                payload.getSearchQuery(), // name
                null,
                payload.categories != null && !payload.categories.isEmpty() ? payload.categories.get(0) : null,
                null,
                payload.minPrice != null ? payload.minPrice.intValue() : null,
                payload.maxPrice != null ? payload.maxPrice.intValue() : null,
                null, // minDiscount
                null, // maxDiscount
                payload.eventTypes != null && !payload.eventTypes.isEmpty() ? payload.eventTypes.get(0) : null, // eventType
                payload.getRating() != null ? payload.getRating().intValue() : null, // rating
                payload.getSortBy(),
                payload.getSortDir(),
                0, // page
                100, // size
                payload.getType(), // type
                false, // limitTo10
                payload.isIgnoreCityFilter() // ignoreCityFilter
        ).enqueue(new Callback<List<GetHomepageSolutionDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<GetHomepageSolutionDTO>> call, @NonNull Response<List<GetHomepageSolutionDTO>> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    allItems.clear();
                    allItems.addAll(response.body());
                    currentPage = 0;
                    updateRecyclerView();
                } else {
                    // Handle API error, e.g., show a message
                    Toast.makeText(requireContext(), "Failed to load solutions.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<GetHomepageSolutionDTO>> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Error loading solutions: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void showFilterDialog() {
        new SolutionFilterFragment().show(getChildFragmentManager(), "SolutionFilterDialog");
    }

    @Override
    protected void addSearchChip(String query) {
        if (query == null || query.isEmpty()) return;
        filterViewModel.setSearchQuery(query);
        filterViewModel.applyNow();
    }


    protected void updateChips(HomeSolutionFilterViewModel.FilterPayload p) {
        chipGroup.removeAllViews();

        if (p.getSearchQuery() != null && !p.getSearchQuery().isEmpty()) {
            addFilterChip("Search: " + p.getSearchQuery(), () -> {
                filterViewModel.setSearchQuery(null);
                filterViewModel.applyNow();
            });
        }

        if (p.categories != null && !p.categories.isEmpty()) {
            addFilterChip("Category: " + p.categories.get(0), () -> {
                filterViewModel.setSelectedCategories(new ArrayList<>());
                filterViewModel.applyNow();
            });
        }
        if (p.eventTypes != null && !p.eventTypes.isEmpty()) {
            addFilterChip("Event Type: " + p.eventTypes.get(0), () -> {
                filterViewModel.setSelectedEventTypes(new ArrayList<>());
                filterViewModel.applyNow();
            });
        }
        if (p.minPrice != null || p.maxPrice != null) {
            String priceText = String.format(Locale.getDefault(), "Price: %.0f-%.0f",
                    p.minPrice != null ? p.minPrice : 0,
                    p.maxPrice != null ? p.maxPrice : Double.MAX_VALUE);
            addFilterChip(priceText, () -> {
                filterViewModel.setMinPrice(null);
                filterViewModel.setMaxPrice(null);
                filterViewModel.applyNow();
            });
        }
        if (p.getRating() != null) {
            addFilterChip("Rating: " + p.getRating(), () -> {
                filterViewModel.setRating(null);
                filterViewModel.applyNow();
            });
        }
        if (p.getSortBy() != null) {
            addFilterChip("Sort by: " + p.getSortBy(), () -> {
                filterViewModel.setSortBy(null);
                filterViewModel.setSortDir(null);
                filterViewModel.applyNow();
            });
        }
        if (p.getType() != null && !p.getType().isEmpty()) {
            String displayType = p.getType().equals("PRODUCT") ? "Products" : "Services";
            addFilterChip("Type: " + displayType, () -> {
                filterViewModel.setType(null);
                if (solutionTypeRadioGroup != null) {
                    solutionTypeRadioGroup.clearCheck();
                    solutionTypeRadioGroup.check(R.id.radioAll);
                }
                filterViewModel.applyNow();
            });
        }
    }

    @Override
    protected void resetFilters() {
        ((HomeSolutionFilterViewModel) filterViewModel).resetFilters();
    }
}