package com.example.eventplanner.fragments.gallery;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.ImageAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ImagePicker extends DialogFragment {

    private static final int REQUEST_CODE = 100;
    private ArrayList<Uri> selectedImageUris = new ArrayList<>();
    private RecyclerView imageRecyclerView;
    private TextView noImagesText;
    private ImageAdapter imageAdapter;

    public interface ImageDialogListener {
        void onImagesSelected(List<Uri> images);
    }

    private ImageDialogListener listener;

    public void setImageDialogListener(ImageDialogListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View view = requireActivity().getLayoutInflater().inflate(R.layout.dialog_add_images, null);
        builder.setView(view);

        Button selectImagesBtn = view.findViewById(R.id.selectImagesBtn);
        imageRecyclerView = view.findViewById(R.id.imageRecyclerView);
        noImagesText = view.findViewById(R.id.noImagesText);

        imageAdapter = new ImageAdapter(requireContext(), selectedImageUris, position -> {
            selectedImageUris.remove(position);
            imageAdapter.notifyDataSetChanged();
            togglePreview();
        });

        imageRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        imageRecyclerView.setAdapter(imageAdapter);

        selectImagesBtn.setOnClickListener(v -> openImagePicker());

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton("Done", (dialog, which) -> {
            if (listener != null) {
                listener.onImagesSelected(selectedImageUris);
            }
        });

        return builder.create();
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Images"), REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri uri = data.getClipData().getItemAt(i).getUri();
                    selectedImageUris.add(uri);
                }
            } else if (data.getData() != null) {
                selectedImageUris.add(data.getData());
            }
            imageAdapter.notifyDataSetChanged();
            togglePreview();
        }
    }

    private void togglePreview() {
        if (selectedImageUris.isEmpty()) {
            imageRecyclerView.setVisibility(View.GONE);
            noImagesText.setVisibility(View.VISIBLE);
        } else {
            imageRecyclerView.setVisibility(View.VISIBLE);
            noImagesText.setVisibility(View.GONE);
        }
    }

}

