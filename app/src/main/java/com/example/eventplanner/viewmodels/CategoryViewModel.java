package com.example.eventplanner.viewmodels;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.example.eventplanner.dto.solutioncategory.GetSolutionCategoryDTO;
import com.example.eventplanner.utils.ClientUtils;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryViewModel extends AndroidViewModel {

    private final MutableLiveData<List<GetSolutionCategoryDTO>> activeCategories = new MutableLiveData<>();
    private final MutableLiveData<List<GetSolutionCategoryDTO>> recommendedCategories = new MutableLiveData<>();

    public CategoryViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<GetSolutionCategoryDTO>> getActiveCategories() {
        return activeCategories;
    }

    public MutableLiveData<List<GetSolutionCategoryDTO>> getRecommendedCategories() {
        return recommendedCategories;
    }

    public void fetchActiveCategories() {
        String auth = ClientUtils.getAuthorization(getApplication());
        if (auth.isEmpty()) {
            Toast.makeText(getApplication(), "User is not authenticated.", Toast.LENGTH_SHORT).show();
            activeCategories.setValue(new ArrayList<>());
            return;
        }

        Call<List<GetSolutionCategoryDTO>> call = ClientUtils.solutionCategoryService.getAllAccepted(auth);
        call.enqueue(new Callback<List<GetSolutionCategoryDTO>>() {
            @Override
            public void onResponse(Call<List<GetSolutionCategoryDTO>> call, Response<List<GetSolutionCategoryDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    activeCategories.setValue(response.body());
                } else {
                    activeCategories.setValue(new ArrayList<>());
                    Log.e("API_CALL", "Unsuccessful response for active categories: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<GetSolutionCategoryDTO>> call, Throwable t) {
                activeCategories.setValue(new ArrayList<>());
                Log.e("API_CALL", "Network error while fetching active categories: " + t.getMessage());
            }
        });
    }

    public void fetchRecommendedCategories() {
        String auth = ClientUtils.getAuthorization(getApplication());
        if (auth.isEmpty()) {
            Toast.makeText(getApplication(), "Korisnik nije autentifikovan.", Toast.LENGTH_SHORT).show();
            recommendedCategories.setValue(new ArrayList<>());
            return;
        }

        Call<List<GetSolutionCategoryDTO>> call = ClientUtils.solutionCategoryService.getAllRecommended(auth);
        call.enqueue(new Callback<List<GetSolutionCategoryDTO>>() {
            @Override
            public void onResponse(Call<List<GetSolutionCategoryDTO>> call, Response<List<GetSolutionCategoryDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    recommendedCategories.setValue(response.body());
                } else {
                    recommendedCategories.setValue(new ArrayList<>());
                    Log.e("API_CALL", "Unsuccessful response for recommended categories: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<GetSolutionCategoryDTO>> call, Throwable t) {
                recommendedCategories.setValue(new ArrayList<>());
                Log.e("API_CALL", "Network error while fetching recommended categories: " + t.getMessage());
            }
        });
    }
}