package com.example.eventplanner.activities.business;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.example.eventplanner.activities.homepage.ProviderHomepageActivity;
import com.example.eventplanner.dto.business.GetBusinessDTO;
import com.example.eventplanner.dto.user.GetUserDTO;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusinessInfoActivity extends AppCompatActivity {
    private String companyEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_business_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getCurrentBusiness();

        Button deactivateBtn = findViewById(R.id.deactivateButton);
        deactivateBtn.setOnClickListener(v -> {
            deactivateBusiness(v);
        });
    }


    public void closeForm(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }



    public void deactivateBusiness(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Deactivate business?");
        dialog.setMessage("Are you sure you want to deactivate your business account?");

        dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deactivate();
            }
        });

        dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = dialog.create();
        alert.show();
    }


    public void openBusinessEditFragment(View view) {
        Intent intent = new Intent(BusinessInfoActivity.this, BusinessEditActivity.class);
        startActivity(intent);
    }



    private void setUpFormDetails(GetBusinessDTO getBusinessDTO) {

        TextView name = findViewById(R.id.name);
        name.setText(getBusinessDTO.getCompanyName());

        TextView email = findViewById(R.id.email);
        email.setText(getBusinessDTO.getCompanyEmail());

        TextView address = findViewById(R.id.address);
        address.setText(getBusinessDTO.getAddress());

        TextView phone = findViewById(R.id.phone);
        phone.setText(getBusinessDTO.getPhone());

        TextView description = findViewById(R.id.description);
        description.setText(getBusinessDTO.getDescription());

    }



    private void getCurrentBusiness() {
        String authorization = ClientUtils.getAuthorization(this);

        Call<GetBusinessDTO> call = ClientUtils.businessService.getBusinessForCurrentUser(authorization);

        call.enqueue(new Callback<GetBusinessDTO>() {
            @Override
            public void onResponse(Call<GetBusinessDTO> call, Response<GetBusinessDTO> response) {
                if (response.isSuccessful()) {
                    GetBusinessDTO dto = response.body();

                    companyEmail = dto.getCompanyEmail();

                    if (companyEmail == null) {
                        Toast.makeText(BusinessInfoActivity.this, "You don't have an active " +
                                "business account!", Toast.LENGTH_SHORT).show();
                    }

                    setUpFormDetails(dto);
                }
                else if (response.code() == 204 || response.body() == null) {
                    Toast.makeText(BusinessInfoActivity.this, "You don't have an active " +
                            "business account!", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<GetBusinessDTO> call, Throwable t) {
                Toast.makeText(BusinessInfoActivity.this, "Failed to load business information!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void deactivate() {
        if (companyEmail == null) {
            Toast.makeText(BusinessInfoActivity.this, "You cannot deactivate inactive business!", Toast.LENGTH_SHORT).show();
        }
        else {
            String authorization = ClientUtils.getAuthorization(this);

            Call<ResponseBody> call = ClientUtils.businessService.deactivateBusiness(authorization, companyEmail);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(BusinessInfoActivity.this, "Deactivated business account!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(BusinessInfoActivity.this, ProviderHomepageActivity.class);
                        startActivity(intent);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(BusinessInfoActivity.this, "Failed to deactivate business account!", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }


}