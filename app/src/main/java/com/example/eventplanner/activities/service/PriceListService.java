package com.example.eventplanner.activities.service;

import com.example.eventplanner.dto.pricelist.GetPriceListDTO;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface PriceListService {
    @GET("pricelist/{type}")
    Call<GetPriceListDTO> getPriceListByProvider(
            @Header("Authorization") String token,
            @Path("type") String type
    );
}
