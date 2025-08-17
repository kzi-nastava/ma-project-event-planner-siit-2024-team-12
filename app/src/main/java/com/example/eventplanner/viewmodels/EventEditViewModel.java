package com.example.eventplanner.viewmodels;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventplanner.dto.agenda.CreateActivityDTO;
import com.example.eventplanner.dto.event.CreateEventDTO;
import com.example.eventplanner.dto.event.EventDetailsDTO;
import com.example.eventplanner.dto.location.CreateLocationDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// used for setting event attributes across different fragments
public class EventEditViewModel extends AndroidViewModel {
    private final MutableLiveData<EventDetailsDTO> dto = new MutableLiveData<>(new EventDetailsDTO());
    public EventEditViewModel(Application application) { super(application); }

    public LiveData<EventDetailsDTO> getDto() { return dto; }



    public void updateAgenda(CreateActivityDTO newActivity, Integer position) {
        EventDetailsDTO current = dto.getValue();

        if (current != null) {
            List<CreateActivityDTO> existingActivities = current.getActivities();
            if (existingActivities == null) {
                existingActivities = new ArrayList<>();
            } else {
                existingActivities = new ArrayList<>(existingActivities);
            }

            if (position != null && position >= 0 && position < existingActivities.size()) {
                existingActivities.set(position, newActivity);
            } else {
                existingActivities.add(newActivity);
            }

            current.setActivities(existingActivities);
            dto.setValue(current);
        }
    }





    public void updateEventAttributes(String key, String value) {
        EventDetailsDTO current = dto.getValue();

        Log.d("CURRENT ", " " + current);

        if (current != null) {
            switch (key) {
                case "id":
                    current.setId(Long.parseLong(value));
                    break;
                case "eventType":
                    current.setEventType(value);
                    break;
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
                    current.setDate(LocalDate.parse(value));
                    break;
            }
            dto.setValue(current);
        }

    }


    public void deleteActivity(CreateActivityDTO activityDTO) {
        EventDetailsDTO current = dto.getValue();

        if (current != null) {
            List<CreateActivityDTO> activities = current.getActivities();

            if (activities == null) {
                activities = new ArrayList<>();
            } else {
                activities = new ArrayList<>(activities);
            }

            activities.remove(activityDTO);

            current.setActivities(activities);
            dto.setValue(current);
        }
    }
}
