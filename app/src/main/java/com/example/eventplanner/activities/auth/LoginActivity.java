package com.example.eventplanner.activities.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.example.eventplanner.utils.ClientUtils;
import com.example.eventplanner.R;
import com.example.eventplanner.UserRole;
import com.example.eventplanner.utils.ValidationUtils;
import com.example.eventplanner.activities.homepage.AdminHomepageActivity;
import com.example.eventplanner.activities.homepage.OrganiserHomepageActivity;
import com.example.eventplanner.activities.homepage.ProviderHomepageActivity;
import com.example.eventplanner.dto.auth.LogInRequest;
import com.example.eventplanner.dto.auth.UserTokenState;
import com.example.eventplanner.fragments.others.ResetPasswordFragment;

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

        Call<UserTokenState> call = ClientUtils.authService.logIn(request);

        call.enqueue(new Callback<UserTokenState>() {
            @Override
            public void onResponse(Call<UserTokenState> call, Response<UserTokenState> response) {
                if (response.isSuccessful()) {
                    UserTokenState userToken = response.body();
                    String token = userToken.getAccessToken();
                    DecodedJWT decodedJWT = JWT.decode(token);
                    String userRole = decodedJWT.getClaim("role").asString();
                    // email is stored under the "sub" key in jwt
                    String email = decodedJWT.getClaim("sub").asString();

                    SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("token", token);
                    editor.putString("userRole", userRole);
                    editor.putString("email", email);
                    editor.apply();


                    if (userRole.equalsIgnoreCase(UserRole.ROLE_ORGANIZER.toString())) {
                        Intent intent = new Intent(LoginActivity.this, OrganiserHomepageActivity.class);
                        startActivity(intent);
                    }
                    else if (userRole.equalsIgnoreCase(UserRole.ROLE_PROVIDER.toString())) {
                        Intent intent = new Intent(LoginActivity.this, ProviderHomepageActivity.class);
                        startActivity(intent);
                    }
                    else if (userRole.equalsIgnoreCase(UserRole.ROLE_ADMIN.toString())) {
                        Intent intent = new Intent(LoginActivity.this, AdminHomepageActivity.class);
                        startActivity(intent);
                    }
                }
                else if (response.code() == 401) {
                    Toast.makeText(LoginActivity.this, "Wrong password!", Toast.LENGTH_SHORT).show();
                }
                else if (response.code() == 404) {
                    Toast.makeText(LoginActivity.this, "Inactive account!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(LoginActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserTokenState> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Nope", Toast.LENGTH_SHORT).show();
            }
        });

    }

}