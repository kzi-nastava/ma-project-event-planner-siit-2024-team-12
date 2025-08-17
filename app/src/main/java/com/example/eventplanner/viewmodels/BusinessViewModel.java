package com.example.eventplanner.viewmodels;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventplanner.dto.business.CreateBusinessDTO;

import java.util.ArrayList;
import java.util.List;

public class BusinessViewModel extends AndroidViewModel {
    private final MutableLiveData<CreateBusinessDTO> dto = new MutableLiveData<>(new CreateBusinessDTO());

    public BusinessViewModel(Application application) {super(application);}
    private final MutableLiveData<List<Uri>> imageUris = new MutableLiveData<>(new ArrayList<>());

    public LiveData<CreateBusinessDTO> getDto() { return dto; }


    public void update(String key, String value) {
        CreateBusinessDTO current = dto.getValue();

        if (current != null) {
            switch (key) {
                case "name":
                    current.setCompanyName(value);
                    break;
                case "email":
                    current.setCompanyEmail(value);
                    break;
                case "address":
                    current.setAddress(value);
                    break;
                case "phone":
                    current.setPhone(value);
                    break;
                case "description":
                    current.setDescription(value);
                    break;
                case "owner":
                    current.setOwner(value);
                    break;
            }
            dto.setValue(current);
        }
    }


    public LiveData<List<Uri>> getImages() {
        return imageUris;
    }

    public void addImage(Uri imageUri) {
        List<Uri> currentList = imageUris.getValue();
        if (currentList == null) {
            currentList = new ArrayList<>();
        }
        currentList.add(imageUri);
        imageUris.setValue(currentList);
    }

    public void setImages(List<Uri> uris) {
        imageUris.setValue(uris);
    }
}
