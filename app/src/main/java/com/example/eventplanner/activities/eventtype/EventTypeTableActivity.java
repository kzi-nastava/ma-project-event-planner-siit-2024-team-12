package com.example.eventplanner.activities.eventtype;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.eventplanner.R;
import com.example.eventplanner.enumeration.UserRole;
import com.example.eventplanner.activities.homepage.AdminHomepageActivity;
import com.example.eventplanner.activities.homepage.ProviderHomepageActivity;

public class EventTypeTableActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_type_table);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView title = findViewById(R.id.title);
        String adminTitle = getString(R.string.event_types);
        String providerTitle = getString(R.string.provider_event_types);

        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String role = prefs.getString("userRole", UserRole.ROLE_ADMIN.toString());

        if (role.equals(UserRole.ROLE_PROVIDER.toString())) {
            title.setText(providerTitle);
        }
        else {
            title.setText(adminTitle);
        }

    }
}