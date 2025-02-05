package com.example.eventplanner.activities.auth;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventplanner.ClientUtils;
import com.example.eventplanner.R;
import com.example.eventplanner.fragments.others.AccountVerification;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AccountActivation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate_account);

        Button activateBtn = findViewById(R.id.activateBtn);
        String email;

        Uri data = getIntent().getData();
        if (data != null && data.getScheme().equals("http")) {
            email = data.getQueryParameter("email");
        } else {
            email = "";
        }

        activateBtn.setOnClickListener(v -> {
            activateAccount(email);
        });


    }



    private void activateAccount(String email) {
        Call<ResponseBody> call = ClientUtils.authService.activateUserAccount(email);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    AccountVerification accountVerification = new AccountVerification(email);
                    accountVerification.show(getSupportFragmentManager(), "accountVerification");
                    Toast.makeText(AccountActivation.this, "Successful account activation!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(AccountActivation.this, "Failed account activation!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(AccountActivation.this, "Failed account activation!", Toast.LENGTH_SHORT).show();
            }
        });
    }



}