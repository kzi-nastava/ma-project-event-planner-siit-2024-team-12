package com.example.eventplanner.activities.auth;


import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.eventplanner.R;

public class SuspendedActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suspended);

        TextView suspendedTime = findViewById(R.id.suspended_time);

        if (getIntent().getExtras() != null) {
            String days = getIntent().getStringExtra("days");
            String hours = getIntent().getStringExtra("hours");
            String minutes = getIntent().getStringExtra("minutes");

            String remainingTime = String.format("Time remaining until reactivation: %s %s %s", days, hours, minutes);
            suspendedTime.setText(remainingTime);
        }
    }
}