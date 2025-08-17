package com.example.eventplanner.activities.service;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.eventplanner.R;

public class ServiceEditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_service_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        AppCompatButton editButton = findViewById(R.id.saveServiceEdit);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Akcija koja se izvršava na klik
                Toast.makeText(ServiceEditActivity.this, getString(R.string.service_edited), Toast.LENGTH_SHORT).show();
            }
        });

        AppCompatButton deleteButton = findViewById(R.id.saveServiceDelete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Akcija koja se izvršava na klik
                Toast.makeText(ServiceEditActivity.this, getString(R.string.service_deleted), Toast.LENGTH_SHORT).show();
            }
        });




    }


    public void closeForm(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }
}