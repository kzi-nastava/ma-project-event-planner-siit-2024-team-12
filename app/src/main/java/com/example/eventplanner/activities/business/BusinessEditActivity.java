package com.example.eventplanner.activities.business;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.eventplanner.utils.ClientUtils;
import com.example.eventplanner.R;
import com.example.eventplanner.utils.ValidationUtils;
import com.example.eventplanner.dto.business.GetBusinessDTO;
import com.example.eventplanner.dto.business.UpdateBusinessDTO;
import com.example.eventplanner.dto.business.UpdatedBusinessDTO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusinessEditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_business_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadCurrentBusiness();

        Button saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(v -> {
            update();
        });
    }

    public void closeForm(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }


    public void setUpInitialForm(GetBusinessDTO dto) {
        TextView name = findViewById(R.id.name);
        name.setText(dto.getCompanyName());

        TextView email = findViewById(R.id.email);
        email.setText(dto.getCompanyEmail());

        EditText address = findViewById(R.id.address);
        address.setText(dto.getAddress());

        EditText phone = findViewById(R.id.phone);
        phone.setText(dto.getPhone());

        EditText description = findViewById(R.id.description);
        description.setText(dto.getDescription());

    }


    public void loadCurrentBusiness() {
        String authorization = ClientUtils.getAuthorization(this);

        Call<GetBusinessDTO> call = ClientUtils.businessService.getBusinessForCurrentUser(authorization);

        call.enqueue(new Callback<GetBusinessDTO>() {
            @Override
            public void onResponse(Call<GetBusinessDTO> call, Response<GetBusinessDTO> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        setUpInitialForm(response.body());
                    }
                    else {
                        Toast.makeText(BusinessEditActivity.this, "No active business yet!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<GetBusinessDTO> call, Throwable t) {
                Toast.makeText(BusinessEditActivity.this, "Failed to load current business!", Toast.LENGTH_SHORT).show();

            }
        });
    }



    private void updateBusiness(String email, UpdateBusinessDTO dto) {
        String auth = ClientUtils.getAuthorization(this);

        Call<UpdatedBusinessDTO> call = ClientUtils.businessService.update(auth, email, dto);

        call.enqueue(new Callback<UpdatedBusinessDTO>() {
            @Override
            public void onResponse(Call<UpdatedBusinessDTO> call, Response<UpdatedBusinessDTO> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(BusinessEditActivity.this, "Successful business update!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(BusinessEditActivity.this, BusinessInfoActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<UpdatedBusinessDTO> call, Throwable t) {
                Toast.makeText(BusinessEditActivity.this, "Failed to update business!", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void update() {
        EditText address = findViewById(R.id.address);
        EditText phone = findViewById(R.id.phone);
        EditText description = findViewById(R.id.description);

        if (!ValidationUtils.isFieldValid(address, "Address is required!")) return;
        if (!ValidationUtils.isFieldValid(phone, "Phone is required!")) return;
        if (!ValidationUtils.isPhoneValid(phone, phone.getText().toString().trim())) return;
        if (!ValidationUtils.isFieldValid(description, "Description is required!")) return;

        UpdateBusinessDTO updateBusinessDTO = new UpdateBusinessDTO(address.getText().toString().trim(),
                phone.getText().toString().trim(), description.getText().toString().trim(),
                new ArrayList<>());

        TextView email = findViewById(R.id.email);

        updateBusiness(email.getText().toString().trim(), updateBusinessDTO);
    }

}
