package com.example.eventplanner.activities.gallery;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
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

    private RecyclerView recyclerView;
    private GalleryAdapter adapter;
    private String type;
    private Long entityId;
    private static final String BASE_IMAGE_URL = "http://10.0.2.2:8080";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_display);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new GalleryAdapter(new ArrayList<>(), imageUrl -> {
            String relativePath = imageUrl.replace("http://10.0.2.2:8080", "");
            deleteImage(relativePath, imageUrl);

        });

        recyclerView.setAdapter(adapter);

        type = getIntent().getStringExtra("type");
        entityId = getIntent().getLongExtra("id", -1);

        if (type == null || entityId == -1) {
            Toast.makeText(this, "Invalid gallery arguments", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        fetchImages();


        Button addImageButton = findViewById(R.id.addImageButton);
        addImageButton.setOnClickListener(v -> {
            ImagePicker imagePicker = new ImagePicker();
            imagePicker.setImageDialogListener(selectedUris -> {
                ImageHelper.uploadMultipleImages(this, selectedUris, type, entityId,
                        "false", this::fetchImages, () -> {});
            });
            imagePicker.show(getSupportFragmentManager(), "image_picker");
        });


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
                            //Toast.makeText(GalleryDisplayActivity.this, "Image deleted", Toast.LENGTH_SHORT).show();
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
