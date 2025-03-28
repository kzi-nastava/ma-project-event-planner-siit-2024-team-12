package com.example.eventplanner.activities.product;

import com.example.eventplanner.dto.product.CreateProductDTO;
import com.example.eventplanner.dto.product.GetProductDTO;
import com.example.eventplanner.dto.product.UpdateProductDTO;
import com.example.eventplanner.dto.product.UpdatedProductDTO;
import com.example.eventplanner.dto.solution.SolutionFilterParams;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ProductService {
    @POST("products")
    Call<ResponseBody> createProduct(@Header("Authorization") String auth,
                                     @Body CreateProductDTO productDTO);


    @GET("products/{id}")
    Call<GetProductDTO> getProduct(@Header("Authorization") String auth,
                                   @Path("id") Long productId);


    @PUT("products/{id}")
    Call<UpdatedProductDTO> updateProduct(@Header("Authorization") String auth,
                                          @Body UpdateProductDTO updateProductDTO,
                                          @Path("id") Long productId);


    @DELETE("products/{id}")
    Call<ResponseBody> deleteProduct(@Header("Authorization") String auth,
                                     @Path("id") Long productId);



    @GET("products/descriptions")
    Call<List<String>> getProductDescriptions(@Header("Authorization") String auth);


    @POST("products/provided-filter")
    Call<List<GetProductDTO>> filterProvidedProducts(@Header("Authorization") String auth,
                                                     @Body SolutionFilterParams params);


}
