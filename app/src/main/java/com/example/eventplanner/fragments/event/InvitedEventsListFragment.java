package com.example.eventplanner.fragments.event;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.activities.profile.QuickRegisterService;
import com.example.eventplanner.adapters.homepage.ListItemAdapter;
import com.example.eventplanner.dto.event.GetEventDTO;
import com.example.eventplanner.fragments.homepage.BaseListFragment;
import com.example.eventplanner.utils.ClientUtils;
import com.example.eventplanner.viewmodels.EventFilterViewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InvitedEventsListFragment extends BaseListFragment<GetEventDTO, EventFilterViewModel> {

    private QuickRegisterService service;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_base_list;
    }

    @Override
    protected void setupViewModel() {
        filterViewModel = new ViewModelProvider(this).get(EventFilterViewModel.class);
        service = ClientUtils.retrofit.create(QuickRegisterService.class);

        if (listTitle != null) {
            listTitle.setVisibility(View.VISIBLE);
            listTitle.setText(R.string.invited_events_title);
            listTitle.setGravity(Gravity.CENTER);
            listTitle.setTextColor(getResources().getColor(android.R.color.black));
            listTitle.setPadding(0, 48, 0, 48);
        }

        if (onlyFromMyCityBtn != null) {
            onlyFromMyCityBtn.setVisibility(View.GONE);
        }
        if (searchView != null) {
            searchView.setVisibility(View.GONE);
        }
        if (filterButtonsLayout != null) {
            filterButtonsLayout.setVisibility(View.GONE);
        }
        if (chipGroup != null) {
            chipGroup.setVisibility(View.GONE);
        }

        filterViewModel.getAppliedFilters().observe(getViewLifecycleOwner(), this::loadItemsFromBackend);

        filterViewModel.applyNow();
    }


    @Override
    protected RecyclerView.Adapter<?> createAdapter() {
        return new ListItemAdapter(requireContext());
    }

    @Override
    protected void loadItemsFromBackend(Object payloadObj) {
        SharedPreferences sp = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String token = sp.getString("token", null);
        String bearer = token != null ? "Bearer " + token : null;

        service.getInvitedEvents(bearer, 0, 100).enqueue(new Callback<List<GetEventDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<GetEventDTO>> call, @NonNull Response<List<GetEventDTO>> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    allItems.clear();
                    allItems.addAll(response.body());
                    currentPage = 0;
                    updateRecyclerView();
                } else {
                    Toast.makeText(requireContext(), "No invited events to show.", Toast.LENGTH_SHORT).show();
                    Log.d("InvitedEvents", "Error loading events: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<GetEventDTO>> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Greška: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("InvitedEvents", "Network error: ", t);
            }
        });
    }

    @Override
    protected void showFilterDialog() {
        Toast.makeText(requireContext(), "Filters are not available for invited events.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void addSearchChip(String query) {
    }


    @Override
    protected void resetFilters() {
    }
}
