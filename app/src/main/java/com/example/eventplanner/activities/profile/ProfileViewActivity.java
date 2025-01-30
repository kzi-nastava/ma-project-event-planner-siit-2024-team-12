package com.example.eventplanner.activities.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.eventplanner.ClientUtils;
import com.example.eventplanner.R;
import com.example.eventplanner.activities.auth.LoginActivity;
import com.example.eventplanner.activities.homepage.HomepageActivity;
import com.example.eventplanner.activities.homepage.OrganiserHomepageActivity;
import com.example.eventplanner.activities.homepage.ProviderHomepageActivity;
import com.example.eventplanner.dto.user.GetUserDTO;
import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileViewActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_view);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getCurrentUser();

        Button deactivateBtn = findViewById(R.id.deactivateButton);
        deactivateBtn.setOnClickListener(v -> {
            deactivateAccount(v);
        });
    }

    public void openProfileEdit(View view) {
        Intent intent = new Intent(ProfileViewActivity.this, ProfileEditActivity.class);
        startActivity(intent);
    }

    public void closeForm(View view) {
        String role = getUserRole();

        if (role.equalsIgnoreCase("role_organizer")) {
            Intent intent = new Intent(ProfileViewActivity.this, OrganiserHomepageActivity.class);
            startActivity(intent);
        }
        else if (role.equalsIgnoreCase("role_provider")) {
            Intent intent = new Intent(ProfileViewActivity.this, ProviderHomepageActivity.class);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(ProfileViewActivity.this, HomepageActivity.class);
            startActivity(intent);
        }
    }


    public void deactivateAccount(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Deactivate account?");
        dialog.setMessage("Are you sure you want to deactivate your account?");

        dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deactivate();
            }
        });

        dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = dialog.create();
        alert.show();
    }



    private void getCurrentUser() {
        String token = getAuthToken();

        if (token == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<GetUserDTO> call = ClientUtils.authService.getCurrentUser("Bearer " + token);

        call.enqueue(new Callback<GetUserDTO>() {
            @Override
            public void onResponse(Call<GetUserDTO> call, Response<GetUserDTO> response) {
                if (response.isSuccessful()) {
                    GetUserDTO user = response.body();
                    Log.d("API_RESPONSE", "Response: " + new Gson().toJson(user));
                    saveUserRole(user.getRole());
                    setUpFormDetails(user);
                } else {
                    Toast.makeText(ProfileViewActivity.this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetUserDTO> call, Throwable t) {
                Toast.makeText(ProfileViewActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void setUpFormDetails(GetUserDTO getUserDTO) {

        TextView name = findViewById(R.id.name);
        name.setText(getUserDTO.getName());

        TextView surname = findViewById(R.id.surname);
        surname.setText(getUserDTO.getSurname());

        TextView email = findViewById(R.id.email);
        email.setText(getUserDTO.getEmail());

        TextView address = findViewById(R.id.address);
        address.setText(getUserDTO.getAddress());

        TextView phone = findViewById(R.id.phone);
        phone.setText(getUserDTO.getPhone());


    }


    private String getAuthToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("token", null);
    }


    private void saveUserRole(String userRole) {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userRole", userRole);
        editor.apply();
    }

    private String getUserRole() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("userRole", "ROLE_UNREGISTERED_USER");
    }



    private void deactivate() {
        TextView emailField = findViewById(R.id.email);
        String email = emailField.getText().toString();

        String token = getAuthToken();

        if (token == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<ResponseBody> call = ClientUtils.userService.deleteUser("Bearer " + token, email);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProfileViewActivity.this, "Deactivated account!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ProfileViewActivity.this, HomepageActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ProfileViewActivity.this, "Failed to deactivate account!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}