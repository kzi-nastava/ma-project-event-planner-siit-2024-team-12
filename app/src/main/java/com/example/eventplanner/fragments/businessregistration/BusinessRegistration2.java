package com.example.eventplanner.fragments.businessregistration;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.eventplanner.activities.homepage.HomepageActivity;
import com.example.eventplanner.dto.business.CreatedBusinessDTO;
import com.example.eventplanner.fragments.gallery.ImagePicker;
import com.example.eventplanner.utils.ClientUtils;
import com.example.eventplanner.R;
import com.example.eventplanner.utils.ImageHelper;
import com.example.eventplanner.utils.ValidationUtils;
import com.example.eventplanner.activities.business.BusinessInfoActivity;
import com.example.eventplanner.activities.business.BusinessRegistrationActivity;
import com.example.eventplanner.dto.user.GetUserDTO;
import com.example.eventplanner.viewmodels.BusinessViewModel;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BusinessRegistration2 extends Fragment {
    private View view;
    BusinessViewModel viewModel;
    String registrationSuccess;
    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView selectedImage;
    private Button uploadImageButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_business_registration2, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(BusinessViewModel.class);

        registrationSuccess = getString(R.string.business_reg_success);

        Button backButton = view.findViewById(R.id.back2);
        Button registerButton = view.findViewById(R.id.register);

        backButton.setOnClickListener(v -> {
            if (getActivity() instanceof BusinessRegistrationActivity) {
                ((BusinessRegistrationActivity) getActivity()).previousPage();
            }
        });

        registerButton.setOnClickListener(v -> {
            createBusiness();
        });

        return view;

    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        uploadImageButton = view.findViewById(R.id.uploadImageButton);

        uploadImageButton.setOnClickListener(v -> {
            if (ImageHelper.hasImagePermission(requireContext())) {
                openImagePickerDialog();
            } else {
                ImageHelper.requestImagePermission(this);
            }
        });
    }


    private void openImagePickerDialog() {
        ImagePicker dialog = new ImagePicker();
        dialog.setImageDialogListener(images -> {
            for (Uri imageUri : images) {
                viewModel.addImage(imageUri);
            }
            //Toast.makeText(getContext(), "Selected " + images.size() + " images", Toast.LENGTH_SHORT).show();
        });
        dialog.show(getParentFragmentManager(), "imagePicker");
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                selectedImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void getOwner() {
        String authorization = ClientUtils.getAuthorization(requireContext());

        Call<GetUserDTO> call = ClientUtils.authService.getCurrentUser(authorization);

        call.enqueue(new Callback<GetUserDTO>() {
            @Override
            public void onResponse(Call<GetUserDTO> call, Response<GetUserDTO> response) {
                if (response.isSuccessful()) {
                    GetUserDTO user = response.body();
                    viewModel.update("owner", user.getEmail());
                } else {
                    Toast.makeText(getActivity(), "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetUserDTO> call, Throwable t) {
                Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private boolean saveFormData() {
        EditText phoneField = view.findViewById(R.id.phone);
        EditText descriptionField = view.findViewById(R.id.description);

        // validate input data
        if (!ValidationUtils.isFieldValid(phoneField, "Phone is required!")) return false;
        if (!ValidationUtils.isPhoneValid(phoneField, phoneField.getText().toString().trim())) return false;
        if (!ValidationUtils.isFieldValid(descriptionField, "Description is required")) return false;

        // if valid, save
        String phone = phoneField.getText().toString().trim();
        String description = descriptionField.getText().toString().trim();

        viewModel.update("phone", phone);
        viewModel.update("description", description);
        getOwner();

        return true;

    }


    private void createBusiness() {
        if (saveFormData()) {
            String authorization = ClientUtils.getAuthorization(requireActivity());

            Call<ResponseBody> call = ClientUtils.businessService.registerBusiness(authorization, viewModel.getDto().getValue());

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        String json;
                        try {
                            json = response.body().string();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        Gson gson = new Gson();
                        CreatedBusinessDTO dto = gson.fromJson(json, CreatedBusinessDTO.class);

                        Long businessId = dto.getId();
                        uploadBusinessImages(businessId);

                        Intent intent = new Intent(getActivity(), BusinessInfoActivity.class);
                        startActivity(intent);

                        Toast.makeText(getActivity(), registrationSuccess, Toast.LENGTH_SHORT).show();
                    }

                    else if (response.code() == 403) {
                        Toast.makeText(getActivity(), "You already have an active business account!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), HomepageActivity.class);
                        startActivity(intent);
                    }

                    else if (response.code() == 409) {
                        Toast.makeText(getActivity(), "Already taken business email!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(getActivity(), "Failed to register business!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == ImageHelper.REQUEST_CODE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePickerDialog();
            } else {
                Toast.makeText(getContext(), "Permission denied to read images", Toast.LENGTH_SHORT).show();
            }
        }
    }




    private void uploadBusinessImages(Long businessId) {
        List<Uri> imageUris = viewModel.getImages().getValue();

        if (imageUris == null || imageUris.isEmpty()) return;

        ImageHelper.uploadMultipleImages(requireContext(), imageUris, "company", businessId,
                "false", () -> {
                }, () -> {
                });
    }

}