package com.example.eventplanner.fragments.gallery;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.BuildConfig;
import com.example.eventplanner.R;
import com.example.eventplanner.adapters.gallery.GalleryAdapter;
import com.example.eventplanner.utils.ClientUtils;
import com.example.eventplanner.utils.ImageHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GalleryDisplayFragment extends Fragment {
    private String type, ownerEmail, currentUserEmail, currentCompanyEmail;
    private Long entityId;
    private GalleryAdapter adapter;
    private View view;
    private static final String BASE_IMAGE_URL = "http://" + BuildConfig.IP_ADDR + ":8080";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_gallery_display, container, false);

        initData();
        setupUI();

        if (type == null || entityId == -1) {
            Toast.makeText(requireActivity(), "Invalid gallery arguments", Toast.LENGTH_SHORT).show();
            requireActivity().finish();
        }

        setUpTitle();
        fetchImages();

        return view;
    }



    private void initData() {
        Bundle args = getArguments();
        if (args != null) {
            type = args.getString("type");
            entityId = args.getLong("id", -1);
            ownerEmail = args.getString("ownerEmail");
            currentCompanyEmail = args.getString("currentCompanyEmail");
        } else {
            type = null;
            entityId = -1L;
            ownerEmail = null;
            currentCompanyEmail = null;
        }

        SharedPreferences prefs = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        currentUserEmail = prefs.getString("email", "");
    }


    private void setupUI() {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));

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
                Toast.makeText(requireActivity(), "Not authorized for image deletion.", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(adapter);

        Button addImageButton = view.findViewById(R.id.addImageButton);
        if (!canModify) {
            addImageButton.setVisibility(View.GONE);
        } else {
            addImageButton.setOnClickListener(v -> {
                ImagePicker imagePicker = new ImagePicker();
                imagePicker.setImageDialogListener(selectedUris -> {
                    ImageHelper.uploadMultipleImages(requireContext(), selectedUris, type, entityId,
                            "false", this::fetchImages, () -> {});
                });
                imagePicker.show(getParentFragmentManager(), "image_picker");
            });
        }
    }


    private void setUpTitle() {
        TextView titleTextView = view.findViewById(R.id.titleTextView);
        String entityName = getArguments().getString("entityName");
        if (entityName != null && !entityName.isEmpty()) {
            titleTextView.setText(entityName + " photos");
        } else {
            titleTextView.setText("Photos");
        }
    }

    private void fetchImages() {
        String auth = ClientUtils.getAuthorization(requireContext());

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
                    Toast.makeText(requireActivity(), "No images found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Toast.makeText(requireActivity(), "Failed to load images", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void deleteImage(String relativePath, String fullUrl) {
        String auth = ClientUtils.getAuthorization(requireContext());

        ClientUtils.galleryService.deleteImage(auth, type, entityId, relativePath)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            adapter.removeImage(fullUrl);
                        } else {
                            try {
                                String error = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                                Toast.makeText(requireActivity(), "Failed to delete image on server: " + error, Toast.LENGTH_LONG).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(requireActivity(), "Delete failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
