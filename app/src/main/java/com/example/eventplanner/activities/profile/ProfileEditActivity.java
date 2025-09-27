package com.example.eventplanner.activities.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.example.eventplanner.dto.LocationDTO;
import com.example.eventplanner.utils.ClientUtils;
import com.example.eventplanner.R;
import com.example.eventplanner.utils.ImageHelper;
import com.example.eventplanner.utils.ValidationUtils;
import com.example.eventplanner.dto.user.GetUserDTO;
import com.example.eventplanner.dto.user.UpdateUserDTO;
import com.example.eventplanner.dto.user.UpdatedUserDTO;
import com.example.eventplanner.fragments.others.ChangePasswordFragment;

import java.io.IOException;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileEditActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri = null;
    private ImageView profileImage;
    private Long userId = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_edit);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.addImgBtn).setOnClickListener(v -> openImageChooser());

        setUpInitialForm();

        Button saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(v -> {
            update();
        });


        profileImage = findViewById(R.id.profileImage);

    }


    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            profileImage.setImageURI(selectedImageUri);
        }
    }


    public void openChangePassword(View view) {
        ChangePasswordFragment changePasswordFragment = new ChangePasswordFragment();
        changePasswordFragment.show(getSupportFragmentManager(), "changePasswordFragment");
    }

    public void update() {
        EditText nameField = findViewById(R.id.name);
        EditText surnameField = findViewById(R.id.surname);
        EditText phoneField = findViewById(R.id.phone);
        EditText addressField = findViewById(R.id.address);
        TextView emailField = findViewById(R.id.email);

        // in case user tries to delete data completely

        if (!ValidationUtils.isFieldValid(nameField, "Name is required")) return;
        if (!ValidationUtils.isFieldValid(surnameField, "Surname is required")) return;
        if (!ValidationUtils.isFieldValid(phoneField, "Phone is required!")) return;
        if (!ValidationUtils.isPhoneValid(phoneField, phoneField.getText().toString())) return;
        if (!ValidationUtils.isFieldValid(addressField, "Address is required")) return;
        if (!ValidationUtils.isAddressFormatValid(addressField)) return;

        // email is not validated or updated as it is read-only and cannot be changed

        String name = nameField.getText().toString().trim();
        String surname = surnameField.getText().toString().trim();
        String phone = phoneField.getText().toString().trim();

        String address = addressField.getText().toString().trim();
        String[] addressParts = address.split(",");
        LocationDTO location = new LocationDTO("", addressParts[0], addressParts[1], addressParts[2]);

        String email = emailField.getText().toString();
        UpdateUserDTO updateUserDTO = new UpdateUserDTO(name, surname, location, phone, false);

        updateUser(email, updateUserDTO);

    }



    public void updateUser(String email, UpdateUserDTO updateUserDTO) {
        String authorization = ClientUtils.getAuthorization(this);

        Call<UpdatedUserDTO> call = ClientUtils.userService.update(authorization, email, updateUserDTO);

        call.enqueue(new Callback<UpdatedUserDTO>() {
            @Override
            public void onResponse(Call<UpdatedUserDTO> call, Response<UpdatedUserDTO> response) {
                if (response.isSuccessful()) {
                    if (selectedImageUri != null) {
                        uploadProfileImage();
                    } else {
                        openProfileView();
                        Toast.makeText(ProfileEditActivity.this, "Successful update!", Toast.LENGTH_SHORT).show();
                    }
                }
        }



            @Override
            public void onFailure(Call<UpdatedUserDTO> call, Throwable t) {
                Toast.makeText(ProfileEditActivity.this, "Failed update!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setMainImage(GetUserDTO dto) {
        String imageUrl = dto.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            String fullUrl = "http://10.0.2.2:8080" + imageUrl;

            Glide.with(ProfileEditActivity.this)
                    .load(fullUrl)
                    .placeholder(R.drawable.user_logo)
                    .error(R.drawable.user_logo)
                    .into(profileImage);
        }

    }



    public void setUpInitialForm() {
       String authorization = ClientUtils.getAuthorization(this);

        Call<GetUserDTO> call = ClientUtils.authService.getCurrentUser(authorization);

        call.enqueue(new Callback<GetUserDTO>() {
            @Override
            public void onResponse(Call<GetUserDTO> call, Response<GetUserDTO> response) {
                if (response.isSuccessful()) {
                    GetUserDTO dto = response.body();

                    if (dto != null) {
                        userId = dto.getId();

                        EditText nameField = findViewById(R.id.name);
                        nameField.setText(dto.getName());

                        EditText surnameField = findViewById(R.id.surname);
                        surnameField.setText(dto.getSurname());

                        TextView emailField = findViewById(R.id.email);
                        emailField.setText(dto.getEmail());

                        EditText phoneField = findViewById(R.id.phone);
                        phoneField.setText(dto.getPhone());

                        EditText addressField = findViewById(R.id.address);
                        String fullAddress = dto.getLocation().getAddress() + ", " +
                                dto.getLocation().getCity() + ", " + dto.getLocation().getCountry();
                        addressField.setText(fullAddress);

                        setMainImage(dto);
                    }
                }
            }

            @Override
            public void onFailure(Call<GetUserDTO> call, Throwable t) {

            }
        });
    }


    private void uploadProfileImage() {
        if (selectedImageUri == null || userId == null) {
            openProfileView();
            return;
        }

        MultipartBody.Part imagePart;
        try {
            imagePart = ImageHelper.prepareFilePart(this, "files", selectedImageUri);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to prepare image", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody typePart = RequestBody.create(MultipartBody.FORM, "user");
        RequestBody entityIdPart = RequestBody.create(MultipartBody.FORM, String.valueOf(userId));
        RequestBody isMainPart = RequestBody.create(MultipartBody.FORM, "true");

        String auth = ClientUtils.getAuthorization(this);

        ClientUtils.galleryService.uploadImages(auth, typePart, entityIdPart, List.of(imagePart), isMainPart)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(ProfileEditActivity.this, "Profile image uploaded!", Toast.LENGTH_SHORT).show();
                            openProfileView();
                        } else {
                            Toast.makeText(ProfileEditActivity.this, "Upload failed: " + response.code(), Toast.LENGTH_SHORT).show();
                            openProfileView();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(ProfileEditActivity.this, "Upload error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        openProfileView();
                    }
                });
    }


    private void openProfileView() {
        Intent intent = new Intent(ProfileEditActivity.this, ProfileViewActivity.class);
        startActivity(intent);
        finish();
    }

}