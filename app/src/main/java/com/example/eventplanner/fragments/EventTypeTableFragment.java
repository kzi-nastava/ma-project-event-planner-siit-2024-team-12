package com.example.eventplanner.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.ClientUtils;
import com.example.eventplanner.R;
import com.example.eventplanner.UserRole;
import com.example.eventplanner.adapters.EventTypeAdapter;
import com.example.eventplanner.dto.business.GetBusinessDTO;
import com.example.eventplanner.model.GetEventTypeDTO;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventTypeTableFragment extends Fragment {

    String role;
    String companyEmail;

    private RecyclerView eventTypeRecyclerView;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate fragment layout
        return inflater.inflate(R.layout.fragment_event_type, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences prefs = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        role = prefs.getString("userRole", UserRole.ROLE_ADMIN.toString());

        // Initialize RecyclerView
        eventTypeRecyclerView = view.findViewById(R.id.eventTypeRecyclerView);
        eventTypeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        if (role.equalsIgnoreCase(UserRole.ROLE_PROVIDER.toString())) {
            getCurrentBusiness();
        }
        else {
            loadEventTypes();
        }
    }


    public void loadEventTypes() {
        final List<GetEventTypeDTO>[] eventTypes = new List[]{new ArrayList<>()};

        Call<ArrayList<GetEventTypeDTO>> call = ClientUtils.eventTypeService.getAll();
        call.enqueue(new Callback<ArrayList<GetEventTypeDTO>>() {
            @Override
            public void onResponse(Call<ArrayList<GetEventTypeDTO>> call, Response<ArrayList<GetEventTypeDTO>> response) {
                if (response.code() == 200) {
                    Log.d("DOBAVLJENO ", " " + response.body());
                    eventTypes[0] = response.body();

                    // Set adapter
                    EventTypeAdapter adapter = new EventTypeAdapter(eventTypes[0]);
                    eventTypeRecyclerView.setAdapter(adapter);
                }
                else {
                    Log.d("REZ", "NOPE");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<GetEventTypeDTO>> call, Throwable t) {
                Log.e("API_ERROR", "Failed to connect", t);


            }
        });
    }


    private void loadProviderEventTypes() {
        String auth = ClientUtils.getAuthorization(requireContext());
        final List<GetEventTypeDTO>[] eventTypes = new List[]{new ArrayList<>()};

        Call<ArrayList<GetEventTypeDTO>> call = ClientUtils.businessService.getEventTypesByBusiness(auth, companyEmail);

        call.enqueue(new Callback<ArrayList<GetEventTypeDTO>>() {
            @Override
            public void onResponse(Call<ArrayList<GetEventTypeDTO>> call, Response<ArrayList<GetEventTypeDTO>> response) {
                if (response.isSuccessful()) {
                    eventTypes[0] = response.body();

                    EventTypeAdapter adapter = new EventTypeAdapter(eventTypes[0]);
                    eventTypeRecyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<GetEventTypeDTO>> call, Throwable t) {
                Toast.makeText(getActivity(), "Failed to load event types!", Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void getCurrentBusiness() {
        String auth = ClientUtils.getAuthorization(requireContext());

        Call<GetBusinessDTO> call = ClientUtils.businessService.getBusinessForCurrentUser(auth);

        call.enqueue(new Callback<GetBusinessDTO>() {
            @Override
            public void onResponse(Call<GetBusinessDTO> call, Response<GetBusinessDTO> response) {
                if (response.isSuccessful()) {
                    if (response.body().getCompanyEmail() == null) {
                        Toast.makeText(getActivity(), "No event types for your business!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        companyEmail = response.body().getCompanyEmail();
                        loadProviderEventTypes();
                    }
                }
            }

            @Override
            public void onFailure(Call<GetBusinessDTO> call, Throwable t) {
                Toast.makeText(getActivity(), "Failed to load the current business!", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
