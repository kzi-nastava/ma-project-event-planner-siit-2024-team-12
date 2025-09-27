package com.example.eventplanner.activities.service;

import com.example.eventplanner.dto.pricelist.GetPriceListDTO;
import com.example.eventplanner.dto.pricelist.UpdatePriceListSolutionDTO;
import com.example.eventplanner.dto.pricelist.UpdatedPriceListItemDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PriceListService {
    @GET("pricelist/{type}")
    Call<GetPriceListDTO> getPriceListByProvider(
            @Header("Authorization") String token,
            @Path("type") String type
    );
    @PUT("pricelist/{type}/{id}")
    Call<UpdatedPriceListItemDTO> updatePriceListItem(
            @Header("Authorization") String token,
            @Path("type") String type,
            @Path("id") Long id,
            @Body UpdatePriceListSolutionDTO priceListSolution
    );
}
