package com.example.eventplanner.fragments.gallery;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

import java.util.List;


public interface GalleryService {
    @Multipart
    @POST("/api/images")
    Call<ResponseBody> uploadImages(
            @Header("Authorization") String auth,
            @Part("type") RequestBody type,
            @Part("entityId") RequestBody entityId,
            @Part List<MultipartBody.Part> files,
            @Part("isMain") RequestBody isMain
    );

}
