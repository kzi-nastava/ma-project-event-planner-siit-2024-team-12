package com.example.eventplanner.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.eventplanner.dto.pricelist.GetPriceListDTO;
import com.example.eventplanner.dto.pricelist.UpdatePriceListSolutionDTO;
import com.example.eventplanner.dto.pricelist.UpdatedPriceListItemDTO;
import com.example.eventplanner.utils.ClientUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PriceListViewModel extends AndroidViewModel {

    private final MutableLiveData<GetPriceListDTO> priceList = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<UpdatedPriceListItemDTO> updatedItem = new MutableLiveData<>();

    public PriceListViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<GetPriceListDTO> getPriceList() {
        return priceList;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    public LiveData<UpdatedPriceListItemDTO> getUpdatedItem() {
        return updatedItem;
    }

    public void fetchPriceList(String type) {
        String auth = ClientUtils.getAuthorization(getApplication());
        if (auth.isEmpty()) {
            errorMessage.setValue("User not authenticated.");
            return;
        }

        ClientUtils.priceListService.getPriceListByProvider(auth, type).enqueue(new Callback<GetPriceListDTO>() {
            @Override
            public void onResponse(Call<GetPriceListDTO> call, Response<GetPriceListDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    priceList.setValue(response.body());
                } else if (response.code() == 404) {
                    priceList.setValue(null);
                } else {
                    errorMessage.setValue("Error loading price list: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<GetPriceListDTO> call, Throwable t) {
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }
    public void updatePriceListItem(Long id, String type, UpdatePriceListSolutionDTO updateDTO) {
        String auth = ClientUtils.getAuthorization(getApplication());
        if (auth.isEmpty()) {
            errorMessage.setValue("User not authenticated.");
            return;
        }

        ClientUtils.priceListService.updatePriceListItem(auth, type, id, updateDTO).enqueue(new Callback<UpdatedPriceListItemDTO>() {
            @Override
            public void onResponse(Call<UpdatedPriceListItemDTO> call, Response<UpdatedPriceListItemDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updatedItem.setValue(response.body());
                } else {
                    errorMessage.setValue("Error updating item: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<UpdatedPriceListItemDTO> call, Throwable t) {
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }
}