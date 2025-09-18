package com.example.eventplanner.viewmodels;

import android.app.Application;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.dto.business.GetBusinessDTO;
import com.example.eventplanner.dto.eventtype.GetEventTypeDTO;
import com.example.eventplanner.dto.solutioncategory.GetSolutionCategoryDTO;
import com.example.eventplanner.dto.solutionservice.CreateServiceDTO;
import com.example.eventplanner.dto.solutionservice.CreatedServiceDTO;
import com.example.eventplanner.enumeration.ReservationType;
import com.example.eventplanner.utils.ClientUtils;
import com.example.eventplanner.utils.ImageHelper;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceCreationViewModel extends AndroidViewModel {
    private final MutableLiveData<Map<String, Object>> serviceData = new MutableLiveData<>(new HashMap<>());
    private final MutableLiveData<List<GetSolutionCategoryDTO>> serviceCategories = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<GetEventTypeDTO>> eventTypes = new MutableLiveData<>();
    private final MutableLiveData<List<Long>> selectedEventTypeIds = new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<Uri> serviceImageUri = new MutableLiveData<>();
    private final MutableLiveData<List<Integer>> selectedDays = new MutableLiveData<>();
    private final MutableLiveData<LocalTime> selectedFromTime = new MutableLiveData<>();
    private final MutableLiveData<LocalTime> selectedToTime = new MutableLiveData<>();
    private final MutableLiveData<GetBusinessDTO> currentBusiness = new MutableLiveData<>();
    private final MutableLiveData<List<String>> unavailableDates = new MutableLiveData<>(new ArrayList<>());

    public MutableLiveData<List<String>> getUnavailableDates() {
        return unavailableDates;
    }

    public MutableLiveData<GetBusinessDTO> getCurrentBusiness() {
        return currentBusiness;
    }

    public MutableLiveData<List<Integer>> getSelectedDays() {
        return selectedDays;
    }

    public MutableLiveData<LocalTime> getSelectedFromTime() {
        return selectedFromTime;
    }

    public MutableLiveData<LocalTime> getSelectedToTime() {
        return selectedToTime;
    }

    public void setSelectedDays(List<Integer> days) {
        selectedDays.setValue(days);
    }

    public void setSelectedFromTime(LocalTime time) {
        selectedFromTime.setValue(time);
    }

    public void setSelectedToTime(LocalTime time) {
        selectedToTime.setValue(time);
    }

    public MutableLiveData<Uri> getServiceImageUri() {
        return serviceImageUri;
    }

    public void setServiceImageUri(Uri uri) {
        serviceImageUri.setValue(uri);
    }

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
    public void addUnavailableDate(String date) {
        List<String> currentDates = unavailableDates.getValue();
        if (currentDates == null) {
            currentDates = new ArrayList<>();
        }
        if (!currentDates.contains(date)) {
            currentDates.add(date);
            unavailableDates.setValue(currentDates);
        }
    }

    public void removeUnavailableDate(String date) {
        List<String> currentDates = unavailableDates.getValue();
        if (currentDates != null) {
            currentDates.remove(date);
            unavailableDates.setValue(currentDates);
        }
    }
    public void fetchCurrentBusiness() {
        String auth = ClientUtils.getAuthorization(getApplication());

        if (auth.isEmpty()) {
            Toast.makeText(getApplication(), "Korisnik nije autentifikovan.", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<GetBusinessDTO> call = ClientUtils.businessService.getBusinessForCurrentUser(auth);

        call.enqueue(new Callback<GetBusinessDTO>() {
            @Override
            public void onResponse(Call<GetBusinessDTO> call, Response<GetBusinessDTO> response) {
                if (response.isSuccessful()) {
                    GetBusinessDTO business = response.body();
                    if (business != null) {
                        currentBusiness.setValue(business);
                        Log.d("API_CALL", "Successfully fetched business: " + business.getCompanyName());
                        Toast.makeText(getApplication(), "Podaci o biznisu uspešno dobavljeni.", Toast.LENGTH_SHORT).show();
                    } else {
                        currentBusiness.setValue(null);
                        Log.e("API_CALL", "No business found for current user (204 No Content).");
                        Toast.makeText(getApplication(), "Nije pronađen biznis profil.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    currentBusiness.setValue(null);
                    Log.e("API_CALL", "Unsuccessful response from server: " + response.code());
                    Toast.makeText(getApplication(), "Greška pri dobavljanju biznis profila: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetBusinessDTO> call, Throwable t) {
                currentBusiness.setValue(null);
                Log.e("API_CALL", "Network failure while fetching business: " + t.getMessage());
                Toast.makeText(getApplication(), "Greška na mreži: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void mapServiceDataAndCreateService() {
        Map<String, Object> dataMap = serviceData.getValue();
        if (dataMap == null) {
            Log.e("ServiceCreation", "serviceData is null. Cannot map service.");
            return;
        }
//        fetchCurrentBusiness();
        if(currentBusiness==null){
            Log.e("Busines fetch", "currentBusiness is null. Cannot create service.");
            Toast.makeText(getApplication(), "Morate kreirati kompaniju pre kreiranja usluge.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            CreateServiceDTO service = new CreateServiceDTO();

            service.setCategoryId(getMappedValue(dataMap, "categoryId", Long.class));
            service.setName(getMappedValue(dataMap, "name", String.class));
            service.setFixedTime(getDurationInMinutes(dataMap, "fixedTime"));
            service.setMaxTime(getDurationInHours(dataMap, "maxTime"));
            service.setMinTime(getDurationInHours(dataMap, "minTime"));
            service.setPrice(getMappedValue(dataMap, "price", Double.class));
            service.setDiscount(getMappedValue(dataMap, "discount", Double.class));
            service.setReservationDeadline(getMappedValue(dataMap, "reservationDeadline", Integer.class));
            service.setCancellationDeadline(getMappedValue(dataMap, "cancellationDeadline", Integer.class));
            service.setReservationType(getMappedValue(dataMap, "reservationType", ReservationType.class));
            service.setVisible(getMappedValue(dataMap, "isVisible", Boolean.class));
            service.setAvailability(getMappedValue(dataMap, "isAvailable", Boolean.class));
            service.setDescription(getMappedValue(dataMap, "description", String.class));
            service.setSpecifics(getMappedValue(dataMap, "specifics", String.class));
            service.setCity("");
            service.setImageUrl("");
            service.setBusiness(currentBusiness.getValue().getCompanyEmail());

            service.setEventTypeIds(getMappedList(getSelectedEventTypeIds()));
            service.setWorkingDays(getMappedList(getSelectedDays()));
            service.setWorkingHoursStart(selectedFromTime.getValue());
            service.setWorkingHoursEnd(selectedToTime.getValue());


            submitService(service);

        } catch (NullPointerException | ClassCastException e) {
            Log.e("ServiceCreation", "Error mapping service data", e);
            Toast.makeText(getApplication(), "Došlo je do greške sa podacima. Molimo pokušajte ponovo.", Toast.LENGTH_SHORT).show();
        }
    }
    private <T> T getMappedValue(Map<String, Object> dataMap, String key, Class<T> clazz) {
        Object value = dataMap.get(key);
        if (clazz.isInstance(value)) {
            return clazz.cast(value);
        }
        return null;
    }

    private Duration getDurationInMinutes(Map<String, Object> dataMap, String key) {
        Object value = dataMap.get(key);
        if (value instanceof Integer) {
            return Duration.ofMinutes(((Integer) value).longValue());
        } else if (value instanceof Long) {
            return Duration.ofMinutes((Long) value);
        }
        return null;
    }

    private Duration getDurationInHours(Map<String, Object> dataMap, String key) {
        Object value = dataMap.get(key);
        if (value instanceof Integer) {
            return Duration.ofHours(((Integer) value).longValue());
        } else if (value instanceof Long) {
            return Duration.ofHours((Long) value);
        }
        return null;
    }

    private <T> List<T> getMappedList(MutableLiveData<List<T>> liveData) {
        List<T> list = liveData.getValue();
        return list != null ? list : new ArrayList<>();
    }

    public void submitService(CreateServiceDTO serviceDto) {
        String auth = ClientUtils.getAuthorization(getApplication());

        Call<CreatedServiceDTO> call = ClientUtils.serviceSolutionService.createService(auth, serviceDto);

        call.enqueue(new Callback<CreatedServiceDTO>() {
            @Override
            public void onResponse(Call<CreatedServiceDTO> call, Response<CreatedServiceDTO> response) {
                if (response.isSuccessful()) {
                    CreatedServiceDTO createdService = response.body();
                    if (createdService != null) {
                        Log.d("API_CALL", "Successfully created service with ID: " + createdService.getId());

                        Uri imageUri = getServiceImageUri().getValue();
                        if (imageUri != null) {
                            List<Uri> uris = new ArrayList<>();
                            uris.add(imageUri);

                            ImageHelper.uploadMultipleImages(
                                    getApplication(),
                                    uris,
                                    "service",
                                    createdService.getId(),
                                    "true",
                                    () -> {
                                        Toast.makeText(getApplication(), "Usluga i slika uspešno kreirane.", Toast.LENGTH_SHORT).show();
                                    },
                                    () -> {
                                        Toast.makeText(getApplication(), "Usluga kreirana, ali slika nije postavljena.", Toast.LENGTH_SHORT).show();
                                    }
                            );
                        } else {
                            Toast.makeText(getApplication(), "Usluga uspešno kreirana (bez slike).", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("API_CALL", "Service created, but response body is null.");
                        Toast.makeText(getApplication(), "Usluga kreirana, ali bez povratnih podataka.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("API_CALL", "Unsuccessful response from server: " + response.code() + " " + response.message());
                    Toast.makeText(getApplication(), "Greška pri kreiranju usluge: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CreatedServiceDTO> call, Throwable t) {
                Log.e("API_CALL", "Network failure while creating service: " + t.getMessage());
                Toast.makeText(getApplication(), "Greška na mreži: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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