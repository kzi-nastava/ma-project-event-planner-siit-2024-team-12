package com.example.eventplanner.viewmodels;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventplanner.activities.eventtype.EventTypeService;
import com.example.eventplanner.dto.eventtype.GetEventTypeDTO;
import com.example.eventplanner.dto.solutionservice.CreateServiceDTO;
import com.example.eventplanner.dto.solutionservice.GetServiceDTO;
import com.example.eventplanner.dto.solutionservice.UpdateServiceDTO;
import com.example.eventplanner.dto.solutionservice.UpdatedServiceDTO;
import com.example.eventplanner.utils.ClientUtils;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceEditViewModel extends AndroidViewModel {

    private final MutableLiveData<GetServiceDTO> serviceData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isSaving = new MutableLiveData<>(false);
    private final MutableLiveData<ArrayList<GetEventTypeDTO>> eventTypes = new MutableLiveData<>();

    public ServiceEditViewModel(@NonNull Application application) {
        super(application);
    }
    public LiveData<ArrayList<GetEventTypeDTO>> getEventTypes() {
        return eventTypes;
    }

    public MutableLiveData<GetServiceDTO> getServiceData() {
        return serviceData;
    }

    public MutableLiveData<Boolean> getIsSaving() {
        return isSaving;
    }

    /**
     * Dobavlja podatke o usluzi sa servera na osnovu ID-a.
     * @param serviceId ID usluge.
     */
    public void fetchService(Long serviceId) {
        String auth = ClientUtils.getAuthorization(getApplication());
        if (auth.isEmpty()) {
            Log.e("ServiceEditViewModel", "Authentication token is missing.");
            return;
        }

        ClientUtils.serviceSolutionService.getService(auth, serviceId).enqueue(new Callback<GetServiceDTO>() {
            @Override
            public void onResponse(Call<GetServiceDTO> call, Response<GetServiceDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    serviceData.setValue(response.body());
                    Log.d("ServiceEditViewModel", "Service fetched successfully: " + response.body().getName());
                } else {
                    Log.e("ServiceEditViewModel", "Failed to fetch service. Code: " + response.code());
                    Toast.makeText(getApplication(), "Greška pri učitavanju usluge.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetServiceDTO> call, Throwable t) {
                Log.e("ServiceEditViewModel", "Network error while fetching service: " + t.getMessage());
                Toast.makeText(getApplication(), "Greška na mreži pri učitavanju usluge.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void fetchEventTypes() {
        String auth = ClientUtils.getAuthorization(getApplication());
        if (auth.isEmpty()) {
            Log.e("ServiceEditViewModel", "Authentication token is missing.");
            return;
        }

        ClientUtils.eventTypeService.getAllActive(auth).enqueue(new Callback<ArrayList<GetEventTypeDTO>>() {
            @Override
            public void onResponse(Call<ArrayList<GetEventTypeDTO>> call, Response<ArrayList<GetEventTypeDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    eventTypes.setValue(response.body());
                } else {
                    eventTypes.setValue(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<GetEventTypeDTO>> call, Throwable t) {
                eventTypes.setValue(new ArrayList<>());
            }
        });
    }

    public void updateService(Long serviceId, UpdateServiceDTO updateServiceDto, Runnable onSuccess, Runnable onFailure) {
        isSaving.setValue(true);
        String auth = ClientUtils.getAuthorization(getApplication());
        if (auth.isEmpty()) {
            Log.e("ServiceEditViewModel", "Authentication token is missing.");
            isSaving.setValue(false);
            return;
        }

        ClientUtils.serviceSolutionService.updateService(auth, serviceId, updateServiceDto).enqueue(new Callback<UpdatedServiceDTO>() {
            @Override
            public void onResponse(Call<UpdatedServiceDTO> call, Response<UpdatedServiceDTO> response) {
                isSaving.setValue(false);
                if (response.isSuccessful()) {
                    Log.d("ServiceEditViewModel", "Service updated successfully.");
                    if (onSuccess != null) onSuccess.run();
                } else {
                    Log.e("ServiceEditViewModel", "Failed to update service. Code: " + response.code());
                    if (onFailure != null) onFailure.run();
                }
            }

            @Override
            public void onFailure(Call<UpdatedServiceDTO> call, Throwable t) {
                isSaving.setValue(false);
                Log.e("ServiceEditViewModel", "Network error while updating service: " + t.getMessage());
                if (onFailure != null) onFailure.run();
            }
        });
    }

    public void deleteService(Long serviceId, Runnable onSuccess, Runnable onFailure) {
        isSaving.setValue(true);
        String auth = ClientUtils.getAuthorization(getApplication());
        if (auth.isEmpty()) {
            Log.e("ServiceEditViewModel", "Authentication token is missing.");
            isSaving.setValue(false);
            return;
        }

        ClientUtils.serviceSolutionService.deleteService(auth, serviceId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                isSaving.setValue(false);
                if (response.isSuccessful()) {
                    Log.d("ServiceEditViewModel", "Service deleted successfully.");
                    if (onSuccess != null) onSuccess.run();
                } else {
                    Log.e("ServiceEditViewModel", "Failed to delete service. Code: " + response.code());
                    if (onFailure != null) onFailure.run();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                isSaving.setValue(false);
                Log.e("ServiceEditViewModel", "Network error while deleting service: " + t.getMessage());
                if (onFailure != null) onFailure.run();
            }
        });
    }
}