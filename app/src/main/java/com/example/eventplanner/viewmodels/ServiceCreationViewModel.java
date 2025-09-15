package com.example.eventplanner.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.dto.solutioncategory.GetSolutionCategoryDTO;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceCreationViewModel extends ViewModel {
    private final MutableLiveData<Map<String, Object>> serviceData = new MutableLiveData<>(new HashMap<>());
    private final MutableLiveData<List<GetSolutionCategoryDTO>> serviceCategories = new MutableLiveData<>();

    public MutableLiveData<List<GetSolutionCategoryDTO>> getServiceCategories() {
        return serviceCategories;
    }

    public void fetchServiceCategories() {
        // Simulacija poziva bekenda
        // U realnoj aplikaciji ovde bi ste koristili Retrofit, Volley ili sličnu biblioteku
        // za asinhroni poziv servera.
        List<GetSolutionCategoryDTO> categoriesFromServer = Arrays.asList(
                new GetSolutionCategoryDTO("1", "Fotografija", "Opis za fotografiju", "ACTIVE"),
                new GetSolutionCategoryDTO("2", "Muzika", "Opis za muziku", "ACTIVE"),
                new GetSolutionCategoryDTO("3", "Dekoracija", "Opis za dekoraciju", "ACTIVE")
        );
        // Postavite preuzete podatke u LiveData
        serviceCategories.setValue(categoriesFromServer);
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
}