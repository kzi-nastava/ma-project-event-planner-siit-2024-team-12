package com.example.eventplanner.viewmodels;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.dto.eventtype.GetEventTypeDTO;
import com.example.eventplanner.dto.solutioncategory.GetSolutionCategoryDTO;
import com.example.eventplanner.utils.ClientUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceCreationViewModel extends AndroidViewModel {
    private final MutableLiveData<Map<String, Object>> serviceData = new MutableLiveData<>(new HashMap<>());
    private final MutableLiveData<List<GetSolutionCategoryDTO>> serviceCategories = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<GetEventTypeDTO>> eventTypes = new MutableLiveData<>();
    private final MutableLiveData<List<Long>> selectedEventTypeIds = new MutableLiveData<>(new ArrayList<>());

    public MutableLiveData<ArrayList<GetEventTypeDTO>> getEventTypes() {
        return eventTypes;
    }

    public MutableLiveData<List<Long>> getSelectedEventTypeIds() {
        return selectedEventTypeIds;
    }
    public void setSelectedEventTypeIds(ArrayList<Long> ids) {
        selectedEventTypeIds.setValue(ids);
    }

    public MutableLiveData<List<GetSolutionCategoryDTO>> getServiceCategories() {
        return serviceCategories;
    }

    public ServiceCreationViewModel(@NonNull Application application) {
        super(application);
    }

    public void addData(String key, Object value) {
        if (serviceData.getValue() != null) {
            serviceData.getValue().put(key, value);
        }
    }

    public MutableLiveData<Map<String, Object>> getServiceData() {
        return serviceData;
    }

    public void submitService() {
        // Implementirajte logiku za slanje podataka na server ovde
        // serviceData.getValue() sadrži sve podatke
        Map<String, Object> data = serviceData.getValue();
        // Primer: Pozivanje API-ja
        // new ServiceApi().createService(data);
    }

    public void fetchEventTypes() {
        String auth = ClientUtils.getAuthorization(getApplication());

        if (auth.isEmpty()) {
            Toast.makeText(getApplication(), "Korisnik nije autentifikovan.", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<ArrayList<GetEventTypeDTO>> call = ClientUtils.eventTypeService.getAllActive(auth);

        call.enqueue(new Callback<ArrayList<GetEventTypeDTO>>() {
            @Override
            public void onResponse(Call<ArrayList<GetEventTypeDTO>> call, Response<ArrayList<GetEventTypeDTO>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && !response.body().isEmpty()) {
                        eventTypes.setValue(response.body());
                    } else if (response.code() == 204) {
                        eventTypes.setValue(new ArrayList<>());
                        Log.d("API_CALL", "No event types found (204 No Content).");
                        Toast.makeText(getApplication(), "Nema dostupnih tipova događaja.", Toast.LENGTH_SHORT).show();
                    } else {
                        eventTypes.setValue(new ArrayList<>());
                        Toast.makeText(getApplication(), "Greška pri učitavanju tipova događaja.", Toast.LENGTH_SHORT).show();
                        Log.e("API_CALL", "Error: Body is null or empty, code: " + response.code());
                    }
                } else {
                    eventTypes.setValue(new ArrayList<>());
                    Toast.makeText(getApplication(), "Greška: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e("API_CALL", "HTTP Error: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<GetEventTypeDTO>> call, Throwable t) {
                eventTypes.setValue(new ArrayList<>());
                Toast.makeText(getApplication(), "Greška na mreži: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API_CALL", "Greška pri pozivu API-ja", t);
            }
        });
    }
    public void fetchAcceptedCategories() {
        String auth = ClientUtils.getAuthorization(getApplication());

        if (auth.isEmpty()) {
            Toast.makeText(getApplication(), "Korisnik nije autentifikovan.", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<List<GetSolutionCategoryDTO>> call = ClientUtils.solutionCategoryService.getAllAccepted(auth);

        call.enqueue(new Callback<List<GetSolutionCategoryDTO>>() {
            @Override
            public void onResponse(Call<List<GetSolutionCategoryDTO>> call, Response<List<GetSolutionCategoryDTO>> response) {
                if (response.isSuccessful()) {
                    if(response.body()!=null && !response.body().isEmpty()){
                        serviceCategories.setValue(response.body());
                    }else if(response.code()==204){
                        serviceCategories.setValue(new ArrayList<>());
                        Log.d("API_CALL", "No categories found (204 No Content).");
                        Toast.makeText(getApplication(), "Nema dostupnih kategorija.", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getApplication(), "Greška pri učitavanju kategorija.", Toast.LENGTH_SHORT).show();
                        Log.e("API_CALL", "Error: Body is null or empty, code: " + response.code());
                    }
                }
                else {
                     Toast.makeText(getApplication(), "Greška: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<GetSolutionCategoryDTO>> call, Throwable t) {
                Toast.makeText(getApplication(), "Greška na mreži: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API_CALL", "Greška pri pozivu API-ja", t);
            }
        });
    }
}