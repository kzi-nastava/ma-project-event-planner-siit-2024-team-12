package com.example.eventplanner.fragments.product;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.MultiSelectAdapter;
import com.example.eventplanner.dto.eventtype.GetEventTypeDTO;
import com.example.eventplanner.dto.solutioncategory.GetSolutionCategoryDTO;
import com.example.eventplanner.utils.ClientUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SolutionFilterFragment extends DialogFragment {

    private List<String> categoryOptions = new ArrayList<>();
    private List<String> eventTypeOptions = new ArrayList<>();
    private List<String> availabilityOptions = new ArrayList<>();
    private List<String> descriptionOptions = new ArrayList<>();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_solution_filter, container, false);

        loadCategories();
        loadEventTypes();
        loadAvailability();
        loadDescriptions();


        setupFilter(view, R.id.categoryFilter, getString(R.string.category), categoryOptions);
        setupFilter(view, R.id.eventTypeFilter, getString(R.string.event_type_filter), eventTypeOptions);
        setupFilter(view, R.id.availabilityFilter, getString(R.string.availability), availabilityOptions);
        setupFilter(view, R.id.descriptionFilter, getString(R.string.description_filter), descriptionOptions);

        return view;
    }

    private void setupFilter(View parentView, int filterId, String filterName, List<String> options) {
        View filterView = parentView.findViewById(filterId);
        ((TextView) filterView.findViewById(R.id.parameterName)).setText(filterName);

        RecyclerView recyclerView = filterView.findViewById(R.id.options);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new MultiSelectAdapter(options));



        filterView.setOnClickListener(v -> {
            if (recyclerView.getVisibility() == View.GONE) {
                recyclerView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.GONE);
            }
        });
    }



    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            Objects.requireNonNull(getDialog().getWindow()).setLayout(1000, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }



    private void loadCategories() {
        String auth = ClientUtils.getAuthorization(requireContext());

        Call<List<GetSolutionCategoryDTO>> call = ClientUtils.solutionCategoryService.getAllAccepted(auth);
        call.enqueue(new Callback<List<GetSolutionCategoryDTO>>() {
            @Override
            public void onResponse(Call<List<GetSolutionCategoryDTO>> call, Response<List<GetSolutionCategoryDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<GetSolutionCategoryDTO> categoryDTOS = response.body();

                    for (GetSolutionCategoryDTO dto : categoryDTOS) {
                        categoryOptions.add(dto.getName());
                    }
                }
                else {
                    Toast.makeText(getActivity(), "Error loading categories!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<GetSolutionCategoryDTO>> call, Throwable t) {
                Toast.makeText(getActivity(), "Failed to load categories!", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void loadEventTypes() {
        String auth = ClientUtils.getAuthorization(requireContext());

        Call<ArrayList<GetEventTypeDTO>> call = ClientUtils.eventTypeService.getAllActive(auth);
        call.enqueue(new Callback<ArrayList<GetEventTypeDTO>>() {
            @Override
            public void onResponse(Call<ArrayList<GetEventTypeDTO>> call, Response<ArrayList<GetEventTypeDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<GetEventTypeDTO> eventTypeDTOS = response.body();

                    for (GetEventTypeDTO dto : eventTypeDTOS) {
                        eventTypeOptions.add(dto.getName());
                    }
                }
                else {
                    Toast.makeText(getActivity(), "Error loading event types!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<GetEventTypeDTO>> call, Throwable t) {
                Toast.makeText(getActivity(), "Failed to load event types!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAvailability() {
        availabilityOptions = List.of(getString(R.string.available), getString(R.string.unavailable));
    }


    private void loadDescriptions() {
        String auth = ClientUtils.getAuthorization(requireContext());

        Call<List<String>> call = ClientUtils.productService.getProductDescriptions(auth);
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    descriptionOptions.addAll(response.body());
                }
                else {
                    Toast.makeText(getActivity(), "Error loading descriptions!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Toast.makeText(getActivity(), "Failed to load descriptions!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}