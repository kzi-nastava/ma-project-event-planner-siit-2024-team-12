package com.example.eventplanner.viewmodels;


import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import com.example.eventplanner.dto.product.CreateProductDTO;
import com.example.eventplanner.model.CategoryRecommendation;

public class ProductCreationViewModel extends AndroidViewModel {
    private final MutableLiveData<CreateProductDTO> dto = new MutableLiveData<>(new CreateProductDTO());

    public ProductCreationViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<CreateProductDTO> getDto() { return dto; }
    public boolean usedRecommendation = false;
    public boolean isEventTypeSet = false;
    private boolean isAvailable = true;
    private boolean isVisible = true;


    public void updateAttributes(String key, String value) {
        CreateProductDTO current = dto.getValue();

        if (current != null) {
            switch (key) {
                case "name":
                    current.setName(value);
                    break;
                case "description":
                    current.setDescription(value);
                    break;
                case "category":
                    current.setCategory(value);
                    break;
                case "price":
                    current.setPrice(Double.parseDouble(value));
                    break;
                case "discount":
                    current.setDiscount(Double.parseDouble(value));
                    break;
                case "imageUrl":
                    current.setImageUrl(value);
                    break;
                case "available":
                    current.setIsAvailable(Boolean.parseBoolean(value));
                    break;
                case "visible":
                    current.setIsVisible(Boolean.parseBoolean(value));
                    break;
            }
            dto.setValue(current);
        }
    }


    public void updateEventTypes(List<String> eventTypes) {
        CreateProductDTO current = dto.getValue();
        assert current != null;
        current.setEventTypeNames(eventTypes);
        isEventTypeSet = !eventTypes.isEmpty();
        dto.setValue(current);
    }


    public void updateCategoryRecommendation(CategoryRecommendation recommendation) {
        CreateProductDTO current = dto.getValue();
        assert current != null;
        current.setCategoryRecommendation(recommendation);
        usedRecommendation = true;
        dto.setValue(current);
    }


    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }
}
