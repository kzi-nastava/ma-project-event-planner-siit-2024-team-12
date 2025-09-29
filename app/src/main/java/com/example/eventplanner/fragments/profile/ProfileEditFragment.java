package com.example.eventplanner.fragments.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

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

public class ProfileEditFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri = null;
    private ImageView profileImage;
    private Long userId = null;

    private View view;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile_edit, container, false);

        view.findViewById(R.id.addImgBtn).setOnClickListener(v -> openImageChooser());

        setUpInitialForm();

        Button saveBtn = view.findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(v -> {
            update();
        });


        Button changePass = view.findViewById(R.id.changePass);
        changePass.setOnClickListener(this::openChangePassword);

        return view;
    }


    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == AppCompatActivity.RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            ImageView profileImage = view.findViewById(R.id.profileImage);
            profileImage.setImageURI(selectedImageUri);
        }
    }


    public void openChangePassword(View view) {
        ChangePasswordFragment changePasswordFragment = new ChangePasswordFragment();
        changePasswordFragment.show(getParentFragmentManager(), "changePasswordFragment");
    }

    public void update() {
        EditText nameField = view.findViewById(R.id.name);
        EditText surnameField = view.findViewById(R.id.surname);
        EditText phoneField = view.findViewById(R.id.phone);
        EditText addressField = view.findViewById(R.id.address);
        TextView emailField = view.findViewById(R.id.email);

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
        String authorization = ClientUtils.getAuthorization(requireContext());

        Call<UpdatedUserDTO> call = ClientUtils.userService.update(authorization, email, updateUserDTO);

        call.enqueue(new Callback<UpdatedUserDTO>() {
            @Override
            public void onResponse(Call<UpdatedUserDTO> call, Response<UpdatedUserDTO> response) {
                if (response.isSuccessful()) {
                    if (selectedImageUri != null) {
                        uploadProfileImage();
                    } else {
                        openProfileView();
                        Toast.makeText(requireActivity(), "Successful update!", Toast.LENGTH_SHORT).show();
                    }
                }
        }



            @Override
            public void onFailure(Call<UpdatedUserDTO> call, Throwable t) {
                Toast.makeText(requireActivity(), "Failed update!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setMainImage(GetUserDTO dto) {
        profileImage = view.findViewById(R.id.profileImage);
        String imageUrl = dto.getImageUrl();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            String fullUrl = "http://10.0.2.2:8080" + imageUrl;

            Glide.with(this)
                    .load(fullUrl)
                    .placeholder(R.drawable.user_logo)
                    .error(R.drawable.user_logo)
                    .into(profileImage);
        } else {
            profileImage.setImageResource(R.drawable.user_logo);
        }

    }



    public void setUpInitialForm() {
       String authorization = ClientUtils.getAuthorization(requireContext());

        Call<GetUserDTO> call = ClientUtils.authService.getCurrentUser(authorization);

        call.enqueue(new Callback<GetUserDTO>() {
            @Override
            public void onResponse(Call<GetUserDTO> call, Response<GetUserDTO> response) {
                if (response.isSuccessful()) {
                    GetUserDTO dto = response.body();

                    if (dto != null) {
                        userId = dto.getId();

                        EditText nameField = view.findViewById(R.id.name);
                        nameField.setText(dto.getName());

                        EditText surnameField = view.findViewById(R.id.surname);
                        surnameField.setText(dto.getSurname());

                        TextView emailField = view.findViewById(R.id.email);
                        emailField.setText(dto.getEmail());

                        EditText phoneField = view.findViewById(R.id.phone);
                        phoneField.setText(dto.getPhone());

                        EditText addressField = view.findViewById(R.id.address);
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
            imagePart = ImageHelper.prepareFilePart(requireContext(), "files", selectedImageUri);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireActivity(), "Failed to prepare image", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody typePart = RequestBody.create(MultipartBody.FORM, "user");
        RequestBody entityIdPart = RequestBody.create(MultipartBody.FORM, String.valueOf(userId));
        RequestBody isMainPart = RequestBody.create(MultipartBody.FORM, "true");

        String auth = ClientUtils.getAuthorization(requireContext());

        ClientUtils.galleryService.uploadImages(auth, typePart, entityIdPart, List.of(imagePart), isMainPart)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(requireActivity(), "Profile image uploaded!", Toast.LENGTH_SHORT).show();
                            openProfileView();
                        } else {
                            Toast.makeText(requireActivity(), "Upload failed: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(requireActivity(), "Upload error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        openProfileView();
                    }
                });
    }


    private void openProfileView() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment_container, new ProfileViewFragment())
                .commit();
    }

}