package com.example.eventplanner.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.eventplanner.R;
import com.example.eventplanner.fragments.ServiceManagement;

<<<<<<<< HEAD:app/src/main/java/com/example/eventplanner/activities/POServiceOverview.java
public class POServiceOverview extends AppCompatActivity {
========
public class LoginActivity extends AppCompatActivity {
>>>>>>>> origin/develop:app/src/main/java/com/example/eventplanner/activities/LoginActivity.java

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
<<<<<<<< HEAD:app/src/main/java/com/example/eventplanner/activities/POServiceOverview.java
        setContentView(R.layout.activity_po_service_overview);

        Fragment fragment = new ServiceManagement(); // Tvoj fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();

========
        setContentView(R.layout.activity_login);
>>>>>>>> origin/develop:app/src/main/java/com/example/eventplanner/activities/LoginActivity.java
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}