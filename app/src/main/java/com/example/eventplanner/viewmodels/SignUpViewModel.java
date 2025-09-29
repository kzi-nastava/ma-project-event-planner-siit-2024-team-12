package com.example.eventplanner.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventplanner.dto.user.CreateUserDTO;
import com.example.eventplanner.dto.user.UpgradeUserDTO;
import com.example.eventplanner.enumeration.UserRole;

public class SignUpViewModel extends AndroidViewModel {
    private final MutableLiveData<CreateUserDTO> dto = new MutableLiveData<>(new CreateUserDTO());
    private final MutableLiveData<UpgradeUserDTO> upgradeUserDto = new MutableLiveData<>(new UpgradeUserDTO());

    private boolean isUpgrade = false;
    public SignUpViewModel(Application application) { super(application); }
    public LiveData<CreateUserDTO> getDto() { return dto; }
    public LiveData<UpgradeUserDTO> getUpgradeUserDto() {return upgradeUserDto;}

    public void setUpgradeMode(boolean isUpgrade) {
        this.isUpgrade = isUpgrade;
    }


    public void updateSignUpAttributes(String key, String value) {
        if (isUpgrade) {
            UpgradeUserDTO currentUpgrade = upgradeUserDto.getValue();
            if (currentUpgrade == null) currentUpgrade = new UpgradeUserDTO();

            switch (key) {
                case "password":
                    currentUpgrade.setPassword(value);
                    break;
                case "name":
                    currentUpgrade.setName(value);
                    break;
                case "surname":
                    currentUpgrade.setSurname(value);
                    break;
                case "address":
                    currentUpgrade.setAddress(value);
                    break;
                case "phone":
                    currentUpgrade.setPhone(value);
                    break;
                case "role":
                    UserRole role = UserRole.valueOf(value);
                    currentUpgrade.setRole(role);
                    break;
            }
            upgradeUserDto.setValue(currentUpgrade);
        }
        else {
            CreateUserDTO current = dto.getValue();
            if (current == null) current = new CreateUserDTO();

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
