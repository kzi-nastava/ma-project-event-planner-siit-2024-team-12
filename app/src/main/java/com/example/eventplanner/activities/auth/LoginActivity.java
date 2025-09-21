package com.example.eventplanner.activities.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.eventplanner.activities.homepage.HomepageActivity;
import com.example.eventplanner.fragments.profile.SuspendedUserFragment;
import com.example.eventplanner.utils.ClientUtils;
import com.example.eventplanner.R;
import com.example.eventplanner.utils.ValidationUtils;
import com.example.eventplanner.activities.homepage.OrganiserHomepageActivity;
import com.example.eventplanner.dto.auth.LogInRequest;
import com.example.eventplanner.dto.auth.UserTokenState;
import com.example.eventplanner.fragments.others.ResetPasswordFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        loginButton = findViewById(R.id.loginBtn);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText emailField = findViewById(R.id.email);
                EditText passwordField = findViewById(R.id.password);

                if (!ValidationUtils.isFieldValid(emailField, "Email is required!")) return;
                if (!ValidationUtils.isEmailValid(emailField)) return;
                if (!ValidationUtils.isFieldValid(passwordField, "Password is required!")) return;

                logIn();
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


    private LogInRequest getLogInRequest() {
        EditText emailField = findViewById(R.id.email);
        EditText passwordField = findViewById(R.id.password);

        return new LogInRequest(emailField.getText().toString(), passwordField.getText().toString());
    }

    private void logIn() {
        LogInRequest request = getLogInRequest();

        Call<ResponseBody> call = ClientUtils.authService.logIn(request);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String json = response.body().string();
                        Gson gson = new Gson();
                        UserTokenState userToken = gson.fromJson(json, UserTokenState.class);
                        String token = userToken.getAccessToken();
                        DecodedJWT decodedJWT = JWT.decode(token);
                        String userRole = decodedJWT.getClaim("role").asString();
                        String email = decodedJWT.getClaim("sub").asString();

                        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("token", token);
                        editor.putString("userRole", userRole);
                        editor.putString("email", email);
                        editor.apply();

                        Intent intent = new Intent(LoginActivity.this, HomepageActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (IOException e) {
                        Toast.makeText(LoginActivity.this, "Error parsing response.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (response.code() == 403) {
                        try {
                            String json = response.errorBody().string();
                            Gson gson = new Gson();
                            Type type = new TypeToken<Map<String, Object>>(){}.getType();
                            Map<String, Object> suspensionData = gson.fromJson(json, type);

                            double daysDouble = (double) suspensionData.get("days");
                            double hoursDouble = (double) suspensionData.get("hours");
                            double minutesDouble = (double) suspensionData.get("minutes");

                            SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("isSuspended", true);
                            editor.putString("suspensionDays", String.format("%.0f", daysDouble));
                            editor.putString("suspensionHours", String.format("%.0f", hoursDouble));
                            editor.putString("suspensionMinutes", String.format("%.0f", minutesDouble));
                            editor.apply();

                            Intent intent = new Intent(LoginActivity.this, HomepageActivity.class);
                            startActivity(intent);
                            finish();
                        } catch (IOException e) {
                            Toast.makeText(LoginActivity.this, "Error processing suspension data.", Toast.LENGTH_SHORT).show();
                        }
                    } else if (response.code() == 401) {
                        Toast.makeText(LoginActivity.this, "Wrong password!", Toast.LENGTH_SHORT).show();
                    } else if (response.code() == 404) {
                        Toast.makeText(LoginActivity.this, "Inactive account!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}