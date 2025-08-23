package com.example.eventplanner.activities.business;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.eventplanner.activities.gallery.GalleryDisplayActivity;
import com.example.eventplanner.utils.ClientUtils;
import com.example.eventplanner.R;
import com.example.eventplanner.utils.ImageHelper;
import com.example.eventplanner.utils.ValidationUtils;
import com.example.eventplanner.dto.business.GetBusinessDTO;
import com.example.eventplanner.dto.business.UpdateBusinessDTO;
import com.example.eventplanner.dto.business.UpdatedBusinessDTO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusinessEditActivity extends AppCompatActivity {
    private Uri selectedImageUri = null;
    private Long businessId;
    private String businessName;


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


        ImageView addImageButton = findViewById(R.id.addImgBtn);
        addImageButton.setOnClickListener(v -> {
            openGalleryForImage();
        });

        Button galleryBtn = findViewById(R.id.galleryBtn);
        galleryBtn.setOnClickListener(v -> {
            openBusinessGallery();
        });

    }



    private void openBusinessGallery() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String currentUser = prefs.getString("email", "");

        Intent intent = new Intent(this, GalleryDisplayActivity.class);
        intent.putExtra("type", "company");
        intent.putExtra("id", businessId);
        intent.putExtra("entityName", businessName);
        intent.putExtra("ownerEmail", currentUser);
        startActivity(intent);

    }


    private static final int PICK_IMAGE_REQUEST = 1001;

    private void openGalleryForImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    public void setUpInitialForm(GetBusinessDTO dto) {
        businessId = dto.getId();
        businessName = dto.getCompanyName();

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

        setMainImage(dto);

    }


    private void setMainImage(GetBusinessDTO dto) {
        ImageView mainImage = findViewById(R.id.mainImage);
        String imagePath = dto.getMainImageUrl();

        if (imagePath != null && !imagePath.isEmpty()) {
            String fullUrl = "http://10.0.2.2:8080" + imagePath;

            Glide.with(this)
                    .load(fullUrl)
                    .placeholder(R.drawable.user_logo)
                    .error(R.drawable.user_logo)
                    .into(mainImage);
        } else {
            mainImage.setImageResource(R.drawable.user_logo);
        }
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

                    UpdatedBusinessDTO updated = response.body();

                    if (updated != null && selectedImageUri != null) {
                        uploadProfileImage(businessId);
                    } else {
                        startActivity(new Intent(BusinessEditActivity.this, BusinessInfoActivity.class));
                    }
                }
            }

            @Override
            public void onFailure(Call<UpdatedBusinessDTO> call, Throwable t) {
                Toast.makeText(BusinessEditActivity.this, "Failed to update business!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadProfileImage(Long businessId) {
        if (selectedImageUri == null) return;

        MultipartBody.Part imagePart;
        try {
            imagePart = ImageHelper.prepareFilePart(this, "files", selectedImageUri);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to prepare image", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody type = RequestBody.create(MultipartBody.FORM, "company");
        RequestBody entityId = RequestBody.create(MultipartBody.FORM, String.valueOf(businessId));
        RequestBody isMain = RequestBody.create(MultipartBody.FORM, "true");

        String auth = ClientUtils.getAuthorization(this);

        Call<ResponseBody> call = ClientUtils.galleryService.uploadImages(
                auth,
                type,
                entityId,
                List.of(imagePart),
                isMain
        );

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    //Toast.makeText(BusinessEditActivity.this, "Image uploaded!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(BusinessEditActivity.this, BusinessInfoActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(BusinessEditActivity.this, "Upload failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(BusinessEditActivity.this, "Upload error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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

        UpdateBusinessDTO updateBusinessDTO = new UpdateBusinessDTO(
                address.getText().toString().trim(),
                phone.getText().toString().trim(),
                description.getText().toString().trim(),
                new ArrayList<>()
        );

        TextView email = findViewById(R.id.email);

        updateBusiness(email.getText().toString().trim(), updateBusinessDTO);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            ImageView profileImage = findViewById(R.id.mainImage);
            profileImage.setImageURI(selectedImageUri);
        }

    }

}
