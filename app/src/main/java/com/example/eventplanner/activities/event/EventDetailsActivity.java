package com.example.eventplanner.activities.event;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.eventplanner.R;

public class EventDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView exitBtn = findViewById(R.id.exitBtn);
        exitBtn.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });


        TextView name = findViewById(R.id.name);
        TextView eventType = findViewById(R.id.eventType);
        TextView date = findViewById(R.id.date);
        TextView maxGuests = findViewById(R.id.maxGuests);
        TextView description = findViewById(R.id.description);
        TextView location = findViewById(R.id.location);


        Intent intent = getIntent();
        if (intent != null) {
            name.setText(intent.getStringExtra("name"));
            eventType.setText(intent.getStringExtra("eventType"));
            date.setText(intent.getStringExtra("date"));
            maxGuests.setText(intent.getStringExtra("maxGuests"));
            description.setText(intent.getStringExtra("description"));
            location.setText(intent.getStringExtra("location"));
        }
    }
}