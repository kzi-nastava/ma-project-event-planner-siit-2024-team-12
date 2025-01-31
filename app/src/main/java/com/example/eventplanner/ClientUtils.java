package com.example.eventplanner;

import static android.content.Context.MODE_PRIVATE;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.eventplanner.activities.auth.AuthService;
import com.example.eventplanner.activities.business.BusinessService;
import com.example.eventplanner.activities.event.EventService;
import com.example.eventplanner.activities.eventtype.EventTypeService;
import com.example.eventplanner.activities.profile.UserService;
import com.example.eventplanner.activities.solutioncategory.SolutionCategoryService;
import com.example.eventplanner.model.EventType;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClientUtils {

    public static final String SERVICE_API_PATH = "http://" + BuildConfig.IP_ADDR + ":8080/api/";

    public static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(SERVICE_API_PATH)
            .addConverterFactory(GsonConverterFactory.create())
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

}
