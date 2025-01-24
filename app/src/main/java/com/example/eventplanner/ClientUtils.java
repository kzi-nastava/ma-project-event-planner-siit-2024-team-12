package com.example.eventplanner;

import com.example.eventplanner.activities.eventtype.EventTypeService;
import com.example.eventplanner.activities.solutioncategory.SolutionCategoryService;
import com.example.eventplanner.model.EventType;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClientUtils {

    public static final String SERVICE_API_PATH = "http://" + BuildConfig.IP_ADDR + ":8080/api/";

    public static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(SERVICE_API_PATH)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static EventTypeService eventTypeService = retrofit.create(EventTypeService.class);

    public static SolutionCategoryService solutionCategoryService = retrofit.create(SolutionCategoryService.class);
}
