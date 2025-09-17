package com.example.eventplanner.fragments.homepage;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

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

import java.util.List;

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

        if (listTitle != null) listTitle.setVisibility(View.GONE);
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

        filterViewModel.getAppliedFilters().observe(getViewLifecycleOwner(), payload -> {
            loadItemsFromBackend(payload);

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

        String bearer = ClientUtils.getAuthorization(requireContext());

        service.searchSolutions(
                bearer,
                null,
                payload.descriptions.isEmpty() ? null : payload.descriptions.get(0),
                payload.categories.isEmpty() ? null : payload.categories.get(0),
                null, // city
                payload.minPrice != null ? payload.minPrice.intValue() : null,
                payload.maxPrice != null ? payload.maxPrice.intValue() : null,
                null, // minDiscount
                null, // maxDiscount
                payload.eventTypes.isEmpty() ? null : payload.eventTypes.get(0),
                null, // rating
                null, // sortBy
                null, // sortDir
                0, // page
                100, // size
                null, // type
                false, // limitTo10
                false // ignoreCityFilter
        ).enqueue(new Callback<List<GetHomepageSolutionDTO>>() {
            @Override
            public void onResponse(Call<List<GetHomepageSolutionDTO>> call, Response<List<GetHomepageSolutionDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allItems.clear();
                    allItems.addAll(response.body());
                    currentPage = 0;
                    updateRecyclerView();
                } else {

                }
            }

            @Override
            public void onFailure(Call<List<GetHomepageSolutionDTO>> call, Throwable t) {

            }
        });
    }

    @Override
    protected void showFilterDialog() {
        new SolutionFilterFragment().show(getChildFragmentManager(), "SolutionFilterDialog");
    }

    @Override
    protected void addSearchChip(String query) {

    }
}