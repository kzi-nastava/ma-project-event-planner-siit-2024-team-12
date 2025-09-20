package com.example.eventplanner.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.eventplanner.BuildConfig;
import com.example.eventplanner.activities.auth.AuthService;
import com.example.eventplanner.activities.business.BusinessService;
import com.example.eventplanner.activities.charts.ChartService;
import com.example.eventplanner.activities.event.EventService;
import com.example.eventplanner.activities.eventtype.EventTypeService;
import com.example.eventplanner.activities.homepage.HomepageService;
import com.example.eventplanner.activities.product.ProductService;
import com.example.eventplanner.activities.profile.QuickRegisterService;
import com.example.eventplanner.activities.profile.UserService;
import com.example.eventplanner.activities.solutioncategory.SolutionCategoryService;
import com.example.eventplanner.adapters.datetime.LocalDateAdapter;
import com.example.eventplanner.adapters.datetime.LocalTimeAdapter;
import com.example.eventplanner.fragments.gallery.GalleryService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;
import java.time.LocalTime;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClientUtils {

    public static final String SERVICE_API_PATH = "http://" + BuildConfig.IP_ADDR + ":8080/api/";

    // add adapters for proper LocalDate and LocalTime parsing
    static Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
            .create();

    public static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(SERVICE_API_PATH)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();


    private static String getAuthToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("token", null);
    }

    public static String getAuthorization(Context context) {
        String token = getAuthToken(context);

        if (token == null) {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show();
            return "";
        }

        return "Bearer " + token;
    }


    public static EventTypeService eventTypeService = retrofit.create(EventTypeService.class);

    public static SolutionCategoryService solutionCategoryService = retrofit.create(SolutionCategoryService.class);

    public static EventService eventService = retrofit.create(EventService.class);

    public static AuthService authService = retrofit.create(AuthService.class);

    public static UserService userService = retrofit.create(UserService.class);

    public static BusinessService businessService = retrofit.create(BusinessService.class);

    public static ChartService chartService = retrofit.create(ChartService.class);

    public static ProductService productService = retrofit.create(ProductService.class);

    public static GalleryService galleryService = retrofit.create(GalleryService.class);
    public static HomepageService homepageService = retrofit.create(HomepageService.class);

    public static QuickRegisterService quickRegisterService = retrofit.create(QuickRegisterService.class);

}
