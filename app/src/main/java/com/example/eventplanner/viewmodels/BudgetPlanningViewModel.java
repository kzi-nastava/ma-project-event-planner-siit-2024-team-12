package com.example.eventplanner.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventplanner.dto.budget.GetBudgetItemDTO;
import com.example.eventplanner.dto.budget.UpdateBudgetDTO;
import com.example.eventplanner.dto.budget.UpdateBudgetForEventDTO;
import com.example.eventplanner.dto.eventtype.GetEventTypeDTO;
import com.example.eventplanner.dto.solutioncategory.GetCategoryDTO;
import com.example.eventplanner.utils.ClientUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BudgetPlanningViewModel extends AndroidViewModel {

    private final MutableLiveData<UpdateBudgetForEventDTO> budgetDetails = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<List<GetEventTypeDTO>> activeEventTypes = new MutableLiveData<>();
    private final MutableLiveData<GetEventTypeDTO> currentEventType = new MutableLiveData<>();
    private final MutableLiveData<List<String>> suggestedCategories = new MutableLiveData<>();
    private final MutableLiveData<List<GetCategoryDTO>> activeCategories = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateSuccess = new MutableLiveData<>();
    private final MutableLiveData<List<GetBudgetItemDTO>> updatedItems = new MutableLiveData<>();

    public LiveData<Boolean> getUpdateSuccess() {
        return updateSuccess;
    }
    public LiveData<List<GetBudgetItemDTO>> getUpdatedItems() {
        return updatedItems;
    }
    public LiveData<List<String>> getSuggestedCategories() {
        return suggestedCategories;
    }
    public LiveData<List<GetCategoryDTO>> getActiveCategories() {
        return activeCategories;
    }

    public LiveData<GetEventTypeDTO> getCurrentEventType() {
        return currentEventType;
    }

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
                    currentEventType.setValue(response.body().getEventType());
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
    public void fetchSuggestedCategoriesForEventType(String eventTypeName) {
        String auth = ClientUtils.getAuthorization(getApplication());
        if (auth.isEmpty()) {
            errorMessage.setValue("User not authenticated.");
            return;
        }

        ClientUtils.eventTypeService.getSuggestedCategories(auth, eventTypeName).enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    suggestedCategories.setValue(response.body());
                } else {
                    errorMessage.setValue("Error fetching suggested categories: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }
    public void fetchAllActiveCategories() {
        String auth = ClientUtils.getAuthorization(getApplication());
        if (auth.isEmpty()) {
            errorMessage.setValue("User not authenticated.");
            return;
        }

        ClientUtils.solutionCategoryService.getAccepted(auth).enqueue(new Callback<List<GetCategoryDTO>>() {
            @Override
            public void onResponse(Call<List<GetCategoryDTO>> call, Response<List<GetCategoryDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    activeCategories.setValue(response.body());
                } else {
                    errorMessage.setValue("Error fetching active categories: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<GetCategoryDTO>> call, Throwable t) {
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }
    public void updateBudget(List<GetBudgetItemDTO> budgetItems, Long eventId) {
        String auth = ClientUtils.getAuthorization(getApplication());
        if (auth.isEmpty()) {
            errorMessage.setValue("User not authenticated.");
            updateSuccess.setValue(false);
            return;
        }

        List<UpdateBudgetDTO> updateDtos = budgetItems.stream()
                .map(item -> new UpdateBudgetDTO(item.getId(), item.getName(), item.getCost(), Long.parseLong(item.getCategory().getId())))
                .collect(Collectors.toList());

        ClientUtils.eventService.updateBudget(auth, updateDtos, eventId).enqueue(new Callback<List<GetBudgetItemDTO>>() {
            @Override
            public void onResponse(Call<List<GetBudgetItemDTO>> call, Response<List<GetBudgetItemDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updatedItems.setValue(response.body());
                    updateSuccess.setValue(true);
                } else {
                    errorMessage.setValue("Error updating budget: " + response.code());
                    updateSuccess.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<List<GetBudgetItemDTO>> call, Throwable t) {
                errorMessage.setValue("Network error: " + t.getMessage());
                updateSuccess.setValue(false);
            }
        });
    }


}