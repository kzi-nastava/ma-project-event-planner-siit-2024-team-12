package com.example.eventplanner.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.eventplanner.dto.pricelist.GetPriceListDTO;
import com.example.eventplanner.utils.ClientUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PriceListViewModel extends AndroidViewModel {

    private final MutableLiveData<GetPriceListDTO> priceList = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public PriceListViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<GetPriceListDTO> getPriceList() {
        return priceList;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
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
                    priceList.setValue(null); // Prazna lista ako cenovnik ne postoji
                } else {
                    errorMessage.setValue("Greška pri dobavljanju cenovnika: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<GetPriceListDTO> call, Throwable t) {
                errorMessage.setValue("Mrežna greška: " + t.getMessage());
            }
        });
    }
}