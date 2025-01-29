package com.example.eventplanner.activities.profile;

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

import com.example.eventplanner.ClientUtils;
import com.example.eventplanner.R;
import com.example.eventplanner.dto.user.GetUserDTO;
import com.example.eventplanner.dto.user.UpdateUserDTO;
import com.example.eventplanner.dto.user.UpdatedUserDTO;
import com.example.eventplanner.fragments.others.ChangePasswordFragment;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileEditActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageView4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_edit);

        imageView4 = findViewById(R.id.imageView4);

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
            Uri imageUri = data.getData();
            imageView4.setImageURI(imageUri);

        }
    }

    public void openChangePassword(View view) {
        ChangePasswordFragment changePasswordFragment = new ChangePasswordFragment();
        changePasswordFragment.show(getSupportFragmentManager(), "changePasswordFragment");
    }

    public void closeForm(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }



    public void update() {
        EditText nameField = findViewById(R.id.name);
        EditText surnameField = findViewById(R.id.surname);
        EditText phoneField = findViewById(R.id.phone);
        EditText addressField = findViewById(R.id.address);
        TextView emailField = findViewById(R.id.email);

        // in case user tries to delete data completely
        boolean isValid = true;

        if (!validate(nameField, "Name is required")) isValid = false;
        if (!validate(surnameField, "Surname is required")) isValid = false;
        if (!validate(phoneField, "Phone is required!")) isValid = false;
        if (!isValidPhoneNumber(phoneField, phoneField.getText().toString())) isValid = false;
        if (!validate(addressField, "Address is required")) isValid = false;

        // email is not validated or updated as it is read-only and cannot be changed

        if (!isValid) {
            Toast.makeText(this, "Enter proper values!", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = nameField.getText().toString().trim();
        String surname = surnameField.getText().toString().trim();
        String phone = phoneField.getText().toString().trim();
        String address = addressField.getText().toString().trim();

        String email = emailField.getText().toString();
        UpdateUserDTO updateUserDTO = new UpdateUserDTO(name, surname, address, phone, false);

        updateUser(email, updateUserDTO);

    }


    private boolean validate(EditText field, String errorMessage) {
        String value = field.getText().toString().trim();

        if (value.isEmpty()) {
            field.setError(errorMessage);
            field.requestFocus();
            return false;
        }

        return true;
    }

    private boolean isValidPhoneNumber(EditText field, String phoneNumber) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return true;
        }

        try {
            Phonenumber.PhoneNumber parsedNumber = phoneUtil.parse(phoneNumber, "");
            if (!phoneUtil.isValidNumber(parsedNumber)) {
                field.setError("Invalid phone number format!");
                field.requestFocus();
                return false;
            }
            return true;
        } catch (NumberParseException e) {
            field.setError("Invalid phone number format!");
            field.requestFocus();
            return false;
        }
    }




    public void updateUser(String email, UpdateUserDTO updateUserDTO) {
        String token = getAuthToken();

        Log.d("TOKEN_DEBUG", "Token being sent: " + token);

        if (token == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<UpdatedUserDTO> call = ClientUtils.userService.update("Bearer " + token, email, updateUserDTO);

        call.enqueue(new Callback<UpdatedUserDTO>() {
            @Override
            public void onResponse(Call<UpdatedUserDTO> call, Response<UpdatedUserDTO> response) {
                if (response.isSuccessful()) {
                    Intent intent = new Intent(ProfileEditActivity.this, ProfileViewActivity.class);
                    startActivity(intent);

                    Toast.makeText(ProfileEditActivity.this, "Successful update!", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<UpdatedUserDTO> call, Throwable t) {
                Toast.makeText(ProfileEditActivity.this, "Failed update!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void setUpInitialForm() {
        String token = getAuthToken();

        Log.d("TOKEN_DEBUG", "Token being sent: " + token);

        if (token == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<GetUserDTO> call = ClientUtils.authService.getCurrentUser("Bearer " + token);

        call.enqueue(new Callback<GetUserDTO>() {
            @Override
            public void onResponse(Call<GetUserDTO> call, Response<GetUserDTO> response) {
                if (response.isSuccessful()) {
                    GetUserDTO dto = response.body();

                    EditText nameField = findViewById(R.id.name);
                    nameField.setText(dto.getName());

                    EditText surnameField = findViewById(R.id.surname);
                    surnameField.setText(dto.getSurname());

                    TextView emailField = findViewById(R.id.email);
                    emailField.setText(dto.getEmail());

                    EditText phoneField = findViewById(R.id.phone);
                    phoneField.setText(dto.getPhone());

                    EditText addressField = findViewById(R.id.address);
                    addressField.setText(dto.getAddress());
                }
            }

            @Override
            public void onFailure(Call<GetUserDTO> call, Throwable t) {

            }
        });
    }



    private String getAuthToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("token", null);
    }
}