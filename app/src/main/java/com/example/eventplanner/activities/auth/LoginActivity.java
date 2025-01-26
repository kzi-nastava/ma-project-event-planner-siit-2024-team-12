package com.example.eventplanner.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.eventplanner.R;
import com.example.eventplanner.activities.homepage.AdminHomepageActivity;
import com.example.eventplanner.activities.homepage.OrganiserHomepageActivity;
import com.example.eventplanner.activities.homepage.ProviderHomepageActivity;
import com.example.eventplanner.fragments.others.ResetPasswordFragment;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loginButton = findViewById(R.id.appCompatButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHomePage();
            }
        });
    }

    public void openSignUp(View view) {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    public void openHomePage() {
        Intent intent = new Intent(LoginActivity.this, OrganiserHomepageActivity.class);
        startActivity(intent);
    }


    public void openResetPassword(View view) {
        ResetPasswordFragment resetPasswordFragment = new ResetPasswordFragment();
        resetPasswordFragment.show(getSupportFragmentManager(), "resetPasswordFragment");
    }

    public void closeForm(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }


}