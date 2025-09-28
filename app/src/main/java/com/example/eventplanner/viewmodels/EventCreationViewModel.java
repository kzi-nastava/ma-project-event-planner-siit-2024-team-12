package com.example.eventplanner.viewmodels;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventplanner.dto.agenda.CreateActivityDTO;
import com.example.eventplanner.dto.budget.CreateBudgetItemDTO;
import com.example.eventplanner.dto.event.CreateEventDTO;
import com.example.eventplanner.dto.location.CreateLocationDTO;
import com.example.eventplanner.utils.ClientUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// used for setting event attributes across different fragments
public class EventCreationViewModel extends AndroidViewModel {
    private final MutableLiveData<CreateEventDTO> dto = new MutableLiveData<>(new CreateEventDTO());
    public EventCreationViewModel(Application application) { super(application); }

    public LiveData<CreateEventDTO> getDto() { return dto; }

    private boolean isLocationSet = false;
    private boolean isAgendaSet = false;


    public void updateAgenda(CreateActivityDTO newActivity) {
        CreateEventDTO current = dto.getValue();

        if (current != null) {
            List<CreateActivityDTO> existingActivities = current.getAgenda();
            if (existingActivities == null) {
                existingActivities = new ArrayList<>();
            }
            existingActivities.add(newActivity);
            current.setAgenda(existingActivities);
            isAgendaSet = true;
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

    public boolean isLocationSet() { return isLocationSet; }

    public boolean isAgendaSet() { return isAgendaSet; }

    public void setInvitedEmails(Set<String> emails) {
        CreateEventDTO current = dto.getValue();
        if (current != null) {
            current.setInvitedEmails(emails);
            dto.setValue(current);
        }
    }

    public void setInvitationContent(String content) {
        CreateEventDTO current = dto.getValue();
        if (current != null) {
            current.setInvitationContent(content);
            dto.setValue(current);
        }
    }

    public Set<String> getInvitedEmails() {
        return dto.getValue() != null ? dto.getValue().getInvitedEmails() : null;
    }

    public String getInvitationContent() {
        return dto.getValue() != null ? dto.getValue().getInvitationContent() : null;
    }

    public void setBudget(List<CreateBudgetItemDTO> items){
        CreateEventDTO current = dto.getValue();
        current.setBudgetItems(items);
    }

    public void createEventAndHandleCallback(Context context , Runnable onSuccess, Runnable onFailure) {

        String auth = ClientUtils.getAuthorization(context);

        CreateEventDTO eventDto = dto.getValue();

        if (auth.isEmpty() || eventDto == null) {
            Log.e("API_CALL", "Authentication token or event data is missing.");
            Toast.makeText(context, "Missing data to create event.", Toast.LENGTH_SHORT).show();
            if (onFailure != null) onFailure.run();
            return;
        }

        Call<ResponseBody> call = ClientUtils.eventService.createEvent(auth, eventDto);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();

                    if (responseBody != null) {
                        Log.d("API_CALL", "Successfully created event.");
                        Toast.makeText(context, "Successfully created event.", Toast.LENGTH_LONG).show();
                        if (onSuccess != null) onSuccess.run();

                    }
                } else {
                    String errorMessage = "Error creating event: " + response.code();
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : null;
                        if (errorBody != null && !errorBody.isEmpty()) {
                            errorMessage = errorBody;
                        }
                    } catch (Exception e) {
                        Log.e("API_CALL", "Error reading error body: " + e.getMessage());
                    }

                    Log.e("API_CALL", "Unsuccessful response: " + response.code() + ", Message: " + errorMessage);
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                    if (onFailure != null) onFailure.run();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("API_CALL", "Network failure while creating event: " + t.getMessage());
                Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                if (onFailure != null) onFailure.run();
            }
        });
    }

}
