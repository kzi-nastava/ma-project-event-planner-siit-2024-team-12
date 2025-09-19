package com.example.eventplanner.viewmodels;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.eventplanner.dto.solutionservice.CreateServiceDTO;
import com.example.eventplanner.dto.solutionservice.GetServiceDTO;
import com.example.eventplanner.utils.ClientUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceEditViewModel extends AndroidViewModel {

    private final MutableLiveData<GetServiceDTO> serviceData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isSaving = new MutableLiveData<>(false);

    public ServiceEditViewModel(@NonNull Application application) {
        super(application);
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

    /**
     * Ažurira postojeću uslugu na serveru.
     * @param updatedServiceDto DTO sa ažuriranim podacima.
     */
//    public void updateService(CreateServiceDTO updatedServiceDto, Runnable onSuccess, Runnable onFailure) {
//        isSaving.setValue(true);
//        String auth = ClientUtils.getAuthorization(getApplication());
//        if (auth.isEmpty()) {
//            Log.e("ServiceEditViewModel", "Authentication token is missing.");
//            isSaving.setValue(false);
//            return;
//        }
//
//        ClientUtils.serviceSolutionService.updateService(auth, updatedServiceDto).enqueue(new Callback<CreateServiceDTO>() {
//            @Override
//            public void onResponse(Call<CreateServiceDTO> call, Response<CreateServiceDTO> response) {
//                isSaving.setValue(false);
//                if (response.isSuccessful()) {
//                    Log.d("ServiceEditViewModel", "Service updated successfully.");
//                    Toast.makeText(getApplication(), "Usluga uspešno ažurirana.", Toast.LENGTH_SHORT).show();
//                    if (onSuccess != null) onSuccess.run();
//                } else {
//                    Log.e("ServiceEditViewModel", "Failed to update service. Code: " + response.code());
//                    Toast.makeText(getApplication(), "Greška pri ažuriranju usluge.", Toast.LENGTH_SHORT).show();
//                    if (onFailure != null) onFailure.run();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<CreateServiceDTO> call, Throwable t) {
//                isSaving.setValue(false);
//                Log.e("ServiceEditViewModel", "Network error while updating service: " + t.getMessage());
//                Toast.makeText(getApplication(), "Greška na mreži pri ažuriranju.", Toast.LENGTH_SHORT).show();
//                if (onFailure != null) onFailure.run();
//            }
//        });
//    }
//
//    /**
//     * Briše uslugu sa servera.
//     * @param serviceId ID usluge.
//     */
//    public void deleteService(Long serviceId, Runnable onSuccess, Runnable onFailure) {
//        isSaving.setValue(true);
//        String auth = ClientUtils.getAuthorization(getApplication());
//        if (auth.isEmpty()) {
//            Log.e("ServiceEditViewModel", "Authentication token is missing.");
//            isSaving.setValue(false);
//            return;
//        }
//
//        ClientUtils.serviceSolutionService.deleteService(auth, serviceId).enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//                isSaving.setValue(false);
//                if (response.isSuccessful()) {
//                    Log.d("ServiceEditViewModel", "Service deleted successfully.");
//                    Toast.makeText(getApplication(), "Usluga uspešno obrisana.", Toast.LENGTH_SHORT).show();
//                    if (onSuccess != null) onSuccess.run();
//                } else {
//                    Log.e("ServiceEditViewModel", "Failed to delete service. Code: " + response.code());
//                    Toast.makeText(getApplication(), "Greška pri brisanju usluge.", Toast.LENGTH_SHORT).show();
//                    if (onFailure != null) onFailure.run();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
//                isSaving.setValue(false);
//                Log.e("ServiceEditViewModel", "Network error while deleting service: " + t.getMessage());
//                Toast.makeText(getApplication(), "Greška na mreži pri brisanju.", Toast.LENGTH_SHORT).show();
//                if (onFailure != null) onFailure.run();
//            }
//        });
//    }
}