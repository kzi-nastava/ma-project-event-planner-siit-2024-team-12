package com.example.eventplanner.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventplanner.dto.budget.UpdateBudgetForEventDTO;
import com.example.eventplanner.dto.eventtype.GetEventTypeDTO;
import com.example.eventplanner.utils.ClientUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BudgetPlanningViewModel extends AndroidViewModel {

    private final MutableLiveData<UpdateBudgetForEventDTO> budgetDetails = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<List<GetEventTypeDTO>> activeEventTypes = new MutableLiveData<>();

    public BudgetPlanningViewModel(@NonNull Application application) {
        super(application);
    }
    public LiveData<List<GetEventTypeDTO>> getActiveEventTypes() {
        return activeEventTypes;
    }

    public LiveData<UpdateBudgetForEventDTO> getBudgetDetails() {
        return budgetDetails;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void fetchBudgetDetails(Long eventId) {
        String auth = ClientUtils.getAuthorization(getApplication());
        if (auth.isEmpty()) {
            errorMessage.setValue("User not authenticated.");
            return;
        }

        ClientUtils.eventService.getBudgetDetailsByEventId(auth, eventId).enqueue(new Callback<UpdateBudgetForEventDTO>() {
            @Override
            public void onResponse(Call<UpdateBudgetForEventDTO> call, Response<UpdateBudgetForEventDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    budgetDetails.setValue(response.body());
                } else {
                    errorMessage.setValue("Error fetching budget details: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<UpdateBudgetForEventDTO> call, Throwable t) {
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }
    public void fetchActiveEventTypes() {
        String auth = ClientUtils.getAuthorization(getApplication());
        if (auth.isEmpty()) {
            errorMessage.setValue("User not authenticated.");
            return;
        }

        ClientUtils.eventTypeService.getAllActive(auth).enqueue(new Callback<ArrayList<GetEventTypeDTO>>() {
            @Override
            public void onResponse(Call<ArrayList<GetEventTypeDTO>> call, Response<ArrayList<GetEventTypeDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    activeEventTypes.setValue(response.body());
                } else {
                    errorMessage.setValue("Error fetching active event types: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<GetEventTypeDTO>> call, Throwable t) {
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

}