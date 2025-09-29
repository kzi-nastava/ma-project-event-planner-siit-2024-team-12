package com.example.eventplanner.fragments.product;

import com.example.eventplanner.dto.product.CreatedProductPurchaseDTO;

import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ProductPurchaseService {
    @POST("purchase")
    Call<CreatedProductPurchaseDTO> createProductPurchase(
            @Header("Authorization") String authorization,
            @Query("eventId") Long eventId,
            @Query("productId") Long productId
    );
}
