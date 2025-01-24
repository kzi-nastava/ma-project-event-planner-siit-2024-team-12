package com.example.eventplanner.activities.service;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.eventplanner.R;

public class CategoryCreationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category_creation);
        Button backButton = findViewById(R.id.backCatCreate);
        Button submitButton = findViewById(R.id.newCatSubmit);
        String CategoryCreatedMessage = getString(R.string.category_created);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        backButton.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish(); // Zatvara aktivnost i vraća se na fragment
        });

        submitButton.setOnClickListener(v -> {
            Toast.makeText(this, CategoryCreatedMessage, Toast.LENGTH_SHORT).show();
            Intent resultIntent = new Intent();
            setResult(RESULT_OK, resultIntent);
            finish(); // Zatvara aktivnost i vraća na fragment
        });

    }
}