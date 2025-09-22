package com.example.eventplanner.viewmodels;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventplanner.dto.solutioncategory.CreateCategoryDTO;
import com.example.eventplanner.dto.solutioncategory.CreatedCategoryDTO;
import com.example.eventplanner.dto.solutioncategory.GetCategoryDTO;
import com.example.eventplanner.dto.solutioncategory.UpdateCategoryDTO;
import com.example.eventplanner.dto.solutioncategory.UpdatedCategoryDTO;
import com.example.eventplanner.enumeration.Status;
import com.example.eventplanner.utils.ClientUtils;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryViewModel extends AndroidViewModel {

    private final MutableLiveData<List<GetCategoryDTO>> activeCategories = new MutableLiveData<>();
    private final MutableLiveData<List<GetCategoryDTO>> recommendedCategories = new MutableLiveData<>();
    private final MutableLiveData<String> _creationStatus = new MutableLiveData<>();
    public LiveData<String> getCreationStatus() {
        return _creationStatus;
    }

    public CategoryViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<GetCategoryDTO>> getActiveCategories() {
        return activeCategories;
    }

    public MutableLiveData<List<GetCategoryDTO>> getRecommendedCategories() {
        return recommendedCategories;
    }

    public void createCategory(String name, String description) {
        if (name.isEmpty() || description.isEmpty()) {
            _creationStatus.setValue("Please fill in all fields.");
            return;
        }
        String auth = ClientUtils.getAuthorization(getApplication());
        if (auth.isEmpty()) {
            _creationStatus.setValue("User not authenticated.");
            return;
        }

        CreateCategoryDTO category = new CreateCategoryDTO(name, description, Status.ACCEPTED);
        ClientUtils.solutionCategoryService.createCategory(auth, category).enqueue(new Callback<CreatedCategoryDTO>() {
            @Override
            public void onResponse(Call<CreatedCategoryDTO> call, Response<CreatedCategoryDTO> response) {
                if (response.isSuccessful()) {
                    _creationStatus.setValue("Successfully created category!");
                } else {
                    _creationStatus.setValue("Error creating category: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<CreatedCategoryDTO> call, Throwable t) {
                _creationStatus.setValue("Network error: " + t.getMessage());
            }
        });
    }
    public void fetchActiveCategories() {
        String auth = ClientUtils.getAuthorization(getApplication());
        if (auth.isEmpty()) {
            Toast.makeText(getApplication(), "User is not authenticated.", Toast.LENGTH_SHORT).show();
            activeCategories.setValue(new ArrayList<>());
            return;
        }

        Call<List<GetCategoryDTO>> call = ClientUtils.solutionCategoryService.getAccepted(auth);
        call.enqueue(new Callback<List<GetCategoryDTO>>() {
            @Override
            public void onResponse(Call<List<GetCategoryDTO>> call, Response<List<GetCategoryDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    activeCategories.setValue(response.body());
                } else {
                    activeCategories.setValue(new ArrayList<>());
                    Log.e("API_CALL", "Unsuccessful response for active categories: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<GetCategoryDTO>> call, Throwable t) {
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

        Call<List<GetCategoryDTO>> call = ClientUtils.solutionCategoryService.getAllRecommended(auth);
        call.enqueue(new Callback<List<GetCategoryDTO>>() {
            @Override
            public void onResponse(Call<List<GetCategoryDTO>> call, Response<List<GetCategoryDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    recommendedCategories.setValue(response.body());
                } else {
                    recommendedCategories.setValue(new ArrayList<>());
                    Log.e("API_CALL", "Unsuccessful response for recommended categories: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<GetCategoryDTO>> call, Throwable t) {
                recommendedCategories.setValue(new ArrayList<>());
                Log.e("API_CALL", "Network error while fetching recommended categories: " + t.getMessage());
            }
        });
    }
    public void updateCategory(Long id, UpdateCategoryDTO updateCategoryDTO) {
        String auth = ClientUtils.getAuthorization(getApplication());
        if (auth.isEmpty()) {
            Toast.makeText(getApplication(), "User not authenticated.", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<UpdatedCategoryDTO> call = ClientUtils.solutionCategoryService.updateCategory(auth, id, updateCategoryDTO);
        call.enqueue(new Callback<UpdatedCategoryDTO>() {
            @Override
            public void onResponse(Call<UpdatedCategoryDTO> call, Response<UpdatedCategoryDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getApplication(), "Category updated successfully.", Toast.LENGTH_SHORT).show();
                    fetchActiveCategories();
                } else {
                    Toast.makeText(getApplication(), "Failed to update category: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UpdatedCategoryDTO> call, Throwable t) {
                Toast.makeText(getApplication(), "Network error while updating category.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void deleteCategory(Long id) {
        String auth = ClientUtils.getAuthorization(getApplication());
        if (auth.isEmpty()) {
            Toast.makeText(getApplication(), "User not authenticated.", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<Void> call = ClientUtils.solutionCategoryService.deleteCategory(auth, id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplication(), "Category deleted successfully.", Toast.LENGTH_SHORT).show();
                    fetchActiveCategories();
                } else if (response.code() == 409) {
                    Toast.makeText(getApplication(), "Category cannot be deleted as it is associated with existing services/products.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplication(), "Failed to delete category: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplication(), "Network error while deleting category.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void approveCategory(Long id, UpdateCategoryDTO updateDto) {
        String auth = ClientUtils.getAuthorization(getApplication());
        if (auth.isEmpty()) {
            Toast.makeText(getApplication(), "User not authenticated.", Toast.LENGTH_SHORT).show();
            return;
        }
        updateDto.setStatus(Status.ACCEPTED);

        Call<UpdatedCategoryDTO> call = ClientUtils.solutionCategoryService.approveCategory(auth, id, updateDto);
        call.enqueue(new Callback<UpdatedCategoryDTO>() {
            @Override
            public void onResponse(Call<UpdatedCategoryDTO> call, Response<UpdatedCategoryDTO> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplication(), "Category approved successfully.", Toast.LENGTH_SHORT).show();
                    fetchRecommendedCategories();
                } else {
                    Toast.makeText(getApplication(), "Failed to approve category: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UpdatedCategoryDTO> call, Throwable t) {
                Toast.makeText(getApplication(), "Network error while approving category.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void disapproveCategory(Long id, String changeCategoryName) {
        String auth = ClientUtils.getAuthorization(getApplication());
        if (auth.isEmpty()) {
            Toast.makeText(getApplication(), "User not authenticated.", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody emptyBody = RequestBody.create(MediaType.parse("application/json"), "");

        Call<UpdatedCategoryDTO> call = ClientUtils.solutionCategoryService.disapproveCategory(auth, id, changeCategoryName, emptyBody);
        call.enqueue(new Callback<UpdatedCategoryDTO>() {
            @Override
            public void onResponse(Call<UpdatedCategoryDTO> call, Response<UpdatedCategoryDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getApplication(), "Category disapproved and moved to " + changeCategoryName, Toast.LENGTH_LONG).show();
                    fetchRecommendedCategories();
                } else if (response.code() == 404) {
                    Toast.makeText(getApplication(), "Category not found.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplication(), "Failed to disapprove category: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UpdatedCategoryDTO> call, Throwable t) {
                Toast.makeText(getApplication(), "Network error while disapproving category.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}