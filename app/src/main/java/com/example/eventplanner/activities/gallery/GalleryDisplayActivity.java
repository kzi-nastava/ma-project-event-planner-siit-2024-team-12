package com.example.eventplanner.activities.gallery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.gallery.GalleryAdapter;
import com.example.eventplanner.fragments.gallery.ImagePicker;
import com.example.eventplanner.utils.ClientUtils;
import com.example.eventplanner.utils.ImageHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GalleryDisplayActivity extends AppCompatActivity {
    private String type, ownerEmail, currentUserEmail, currentCompanyEmail;
    private Long entityId;
    private GalleryAdapter adapter;
    private static final String BASE_IMAGE_URL = "http://10.0.2.2:8080";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_display);

        initData();
        setupUI();

        if (type == null || entityId == -1) {
            Toast.makeText(this, "Invalid gallery arguments", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setUpTitle();
        fetchImages();
    }

    private void initData() {
        type = getIntent().getStringExtra("type");
        entityId = getIntent().getLongExtra("id", -1);
        ownerEmail = getIntent().getStringExtra("ownerEmail");
        currentCompanyEmail = getIntent().getStringExtra("currentCompanyEmail");

        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        currentUserEmail = prefs.getString("email", "");
    }

    private void setupUI() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        boolean canModify = false;

        if ("product".equals(type)) {
            canModify = currentCompanyEmail != null && currentCompanyEmail.equals(ownerEmail);
        } else {
            canModify = currentUserEmail.equals(ownerEmail);
        }

        boolean finalCanModify = canModify;
        adapter = new GalleryAdapter(new ArrayList<>(), canModify, imageUrl -> {
            if (finalCanModify) {
                String relativePath = imageUrl.replace(BASE_IMAGE_URL, "");
                deleteImage(relativePath, imageUrl);
            } else {
                Toast.makeText(this, "Not authorized for image deletion.", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(adapter);

        Button addImageButton = findViewById(R.id.addImageButton);
        if (!canModify) {
            addImageButton.setVisibility(View.GONE);
        } else {
            addImageButton.setOnClickListener(v -> {
                ImagePicker imagePicker = new ImagePicker();
                imagePicker.setImageDialogListener(selectedUris -> {
                    ImageHelper.uploadMultipleImages(this, selectedUris, type, entityId,
                            "false", this::fetchImages, () -> {});
                });
                imagePicker.show(getSupportFragmentManager(), "image_picker");
            });
        }
    }


    private void setUpTitle() {
        TextView titleTextView = findViewById(R.id.titleTextView);
        String entityName = getIntent().getStringExtra("entityName");
        if (entityName != null && !entityName.isEmpty()) {
            titleTextView.setText(entityName + " photos");
        } else {
            titleTextView.setText("Photos");
        }
    }

    private void fetchImages() {
        String auth = ClientUtils.getAuthorization(this);

        Call<List<String>> call = ClientUtils.galleryService.getImages(auth, type, entityId);

        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> relativePaths = response.body();
                    List<String> fullUrls = new ArrayList<>();

                    for (String path : relativePaths) {
                        fullUrls.add(BASE_IMAGE_URL + path);
                    }

                    adapter.setImageUrls(fullUrls);
                } else {
                    Toast.makeText(GalleryDisplayActivity.this, "No images found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Toast.makeText(GalleryDisplayActivity.this, "Failed to load images", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void deleteImage(String relativePath, String fullUrl) {
        String auth = ClientUtils.getAuthorization(this);

        ClientUtils.galleryService.deleteImage(auth, type, entityId, relativePath)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            adapter.removeImage(fullUrl);
                        } else {
                            try {
                                String error = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                                Toast.makeText(GalleryDisplayActivity.this, "Failed to delete image on server: " + error, Toast.LENGTH_LONG).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(GalleryDisplayActivity.this, "Delete failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
