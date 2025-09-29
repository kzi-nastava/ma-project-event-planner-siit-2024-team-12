package com.example.eventplanner.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageHelper {

    public static final int REQUEST_CODE_PERMISSIONS = 101;

    public static boolean hasImagePermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }
    }


    public static void requestImagePermission(Fragment fragment) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            fragment.requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_CODE_PERMISSIONS);
        } else {
            fragment.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSIONS);
        }
    }

    public static void requestImagePermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_CODE_PERMISSIONS);
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSIONS);
        }
    }


    public static MultipartBody.Part prepareFilePart(Context context, String partName, Uri fileUri) throws IOException {
        ContentResolver resolver = context.getContentResolver();
        InputStream inputStream = resolver.openInputStream(fileUri);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int nRead;
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        byte[] fileBytes = buffer.toByteArray();

        RequestBody requestFile = RequestBody.create(
                MediaType.parse(resolver.getType(fileUri)),
                fileBytes
        );

        return MultipartBody.Part.createFormData(partName, "image.jpg", requestFile);
    }


    public static void uploadMultipleImages(
            Context context,
            List<Uri> selectedUris,
            String type,
            Long entityId,
            String isMain,
            Runnable onSuccess,
            Runnable onFailure
    ) {
        try {
            List<MultipartBody.Part> parts = new ArrayList<>();
            for (Uri uri : selectedUris) {
                MultipartBody.Part part = ImageHelper.prepareFilePart(context, "files", uri);
                parts.add(part);
            }

            RequestBody typePart = RequestBody.create(MultipartBody.FORM, type);
            RequestBody idPart = RequestBody.create(MultipartBody.FORM, String.valueOf(entityId));
            RequestBody isMainPart = RequestBody.create(MultipartBody.FORM, isMain);

            String auth = ClientUtils.getAuthorization(context);
            ClientUtils.galleryService.uploadImages(auth, typePart, idPart, parts, isMainPart)
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                //Toast.makeText(context, "Images uploaded", Toast.LENGTH_SHORT).show();
                                if (onSuccess != null) onSuccess.run();
                            } else {
                                Toast.makeText(context, "Upload failed: " + response.message(), Toast.LENGTH_SHORT).show();
                                if (onFailure != null) onFailure.run();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(context, "Upload failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            if (onFailure != null) onFailure.run();
                        }
                    });

        } catch (IOException e) {
            Toast.makeText(context, "Failed to prepare images", Toast.LENGTH_SHORT).show();
            if (onFailure != null) onFailure.run();
        }
    }

}
