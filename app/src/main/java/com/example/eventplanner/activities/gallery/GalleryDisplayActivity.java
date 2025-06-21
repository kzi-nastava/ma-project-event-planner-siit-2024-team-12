package com.example.eventplanner.activities.gallery;

import android.os.Bundle;
import android.util.Log;
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
import com.example.eventplanner.utils.ClientUtils;

import java.util.ArrayList;
import java.util.List;

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
        adapter = new GalleryAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        type = getIntent().getStringExtra("type");
        entityId = getIntent().getLongExtra("id", -1);

        if (type == null || entityId == -1) {
            Toast.makeText(this, "Invalid gallery arguments", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        fetchImages();
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

}
