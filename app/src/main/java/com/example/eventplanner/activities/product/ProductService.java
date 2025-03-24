package com.example.eventplanner.activities.product;

import com.example.eventplanner.dto.product.CreateProductDTO;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ProductService {
    @POST("products")
    Call<ResponseBody> createProduct(@Header("Authorization") String auth,
                                     @Body CreateProductDTO productDTO);
}
