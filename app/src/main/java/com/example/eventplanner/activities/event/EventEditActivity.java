package com.example.eventplanner.activities.event;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.eventplanner.R;
import com.example.eventplanner.dto.event.EventDetailsDTO;

public class EventEditActivity extends AppCompatActivity {

    EditText name, eventType, date, maxGuests, description, location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        EventDetailsDTO dto = (EventDetailsDTO) intent.getSerializableExtra("dto");

        name = findViewById(R.id.name);
        eventType = findViewById(R.id.eventType);
        date = findViewById(R.id.date);
        maxGuests = findViewById(R.id.maxGuests);
        description = findViewById(R.id.description);
        location = findViewById(R.id.location);

        name.setText(dto.getName());
        eventType.setText(dto.getEventType());
        date.setText(dto.getDate().toString());
        maxGuests.setText(dto.getMaxGuests());
        description.setText(dto.getDescription());
        location.setText(dto.getLocation().getAddress() + ", " + dto.getLocation().getCity() + ", " +
                dto.getLocation().getCountry());


    }
}