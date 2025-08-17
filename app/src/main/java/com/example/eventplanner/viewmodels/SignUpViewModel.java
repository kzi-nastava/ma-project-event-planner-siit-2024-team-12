package com.example.eventplanner.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventplanner.dto.user.CreateUserDTO;

public class SignUpViewModel extends AndroidViewModel {
    private final MutableLiveData<CreateUserDTO> dto = new MutableLiveData<>(new CreateUserDTO());
    public SignUpViewModel(Application application) { super(application); }
    public LiveData<CreateUserDTO> getDto() { return dto; }


    public void updateSignUpAttributes(String key, String value) {
        CreateUserDTO current = dto.getValue();

        if (current != null) {
            switch (key) {
                case "email":
                    current.setEmail(value);
                    break;
                case "password":
                    current.setPassword(value);
                    break;
                case "name":
                    current.setName(value);
                    break;
                case "surname":
                    current.setSurname(value);
                    break;
                case "address":
                    current.setAddress(value);
                    break;
                case "phone":
                    current.setPhone(value);
                    break;
                case "role":
                    current.setRole(value);
                    break;
            }
            dto.setValue(current);
        }
    }

}
