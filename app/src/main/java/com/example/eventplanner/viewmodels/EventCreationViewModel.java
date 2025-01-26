package com.example.eventplanner.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventplanner.dto.agenda.CreateActivityDTO;
import com.example.eventplanner.dto.event.CreateEventDTO;
import com.example.eventplanner.dto.location.CreateLocationDTO;

import java.util.ArrayList;
import java.util.List;

// used for setting event attributes across different fragments
public class EventCreationViewModel extends AndroidViewModel {
    private final MutableLiveData<CreateEventDTO> dto = new MutableLiveData<>(new CreateEventDTO());
    public EventCreationViewModel(Application application) { super(application); }

    public LiveData<CreateEventDTO> getDto() { return dto; }

    private boolean isLocationSet = false;



    public void updateAgenda(CreateActivityDTO newActivity) {
        CreateEventDTO current = dto.getValue();

        if (current != null) {
            List<CreateActivityDTO> existingActivities = current.getAgenda();
            if (existingActivities == null) {
                existingActivities = new ArrayList<>();
            }
            existingActivities.add(newActivity);
            current.setAgenda(existingActivities);

            dto.setValue(current);
        }
    }



    public void updateLocation(CreateLocationDTO locationDTO) {
        CreateEventDTO current = dto.getValue();
        current.setLocation(locationDTO);
        isLocationSet = true;

        dto.setValue(current);
    }

    public void updateEventAttributes(String key, String value) {
        CreateEventDTO current = dto.getValue();

        Log.d("CURRENT ", " " + current);

        if (current != null) {
            switch (key) {
                case "name":
                    current.setName(value);
                    break;
                case "maxGuests":
                    current.setMaxGuests(value);
                    break;
                case "description":
                    current.setDescription(value);
                    break;
                case "date":
                    current.setDate(value);
                    break;
                case "privacy":
                    current.setPrivacyType(value);
                    break;
                case "eventType":
                    current.setEventType(value);
                    break;
                case "organizer":
                    current.setOrganizer(value);
                    break;
            }
            dto.setValue(current);
        }

    }

    public boolean getLocationSet() { return isLocationSet; }

}
