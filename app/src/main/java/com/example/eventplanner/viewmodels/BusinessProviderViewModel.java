package com.example.eventplanner.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventplanner.dto.business.GetBusinessAndProviderDTO;
import com.example.eventplanner.utils.ClientUtils;
import com.google.gson.Gson;

import java.io.IOException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusinessProviderViewModel extends AndroidViewModel {

    private final MutableLiveData<GetBusinessAndProviderDTO> businessProviderDetails = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public BusinessProviderViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<GetBusinessAndProviderDTO> getBusinessProviderDetails() {
        return businessProviderDetails;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void fetchBusinessProviderDetails(String type, Long solutionId) {
        String auth = ClientUtils.getAuthorization(getApplication());

        if(auth.isEmpty()){
            errorMessage.setValue("Please log in first.");
        }
        if (type == null || solutionId == null) {
            errorMessage.setValue("Required parameters are missing.");
            return;
        }

        Call<GetBusinessAndProviderDTO> call = ClientUtils.userService.getBusinessProviderDetails(auth, type, solutionId);

        call.enqueue(new Callback<GetBusinessAndProviderDTO>() {
            @Override
            public void onResponse(Call<GetBusinessAndProviderDTO> call, Response<GetBusinessAndProviderDTO> response) {
                if (response.isSuccessful()) {
                    businessProviderDetails.setValue(response.body());
                } else {
                    String errorMsg = "Failed to fetch details (HTTP " + response.code() + ").";

                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : null;
                        if (errorBody != null && !errorBody.isEmpty()) {
                            errorMsg = errorBody;
                        }
                    } catch (IOException e) {
                        Log.e("API_ERROR", "Error reading error body: " + e.getMessage());
                    }

                    errorMessage.setValue(errorMsg);
                    Log.e("API_CALL", errorMsg);
                }
            }

            @Override
            public void onFailure(Call<GetBusinessAndProviderDTO> call, Throwable t) {
                errorMessage.setValue("Network error: Could not connect to server.");
                Log.e("API_CALL", "Network failure: " + t.getMessage());
            }
        });
    }
}