package com.example.eventplanner.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.eventplanner.BuildConfig;
import com.example.eventplanner.services.AuthService;
import com.example.eventplanner.services.BusinessService;
import com.example.eventplanner.services.ChartService;
import com.example.eventplanner.services.EventService;
import com.example.eventplanner.services.EventTypeService;
import com.example.eventplanner.services.HomepageService;
import com.example.eventplanner.services.ProductService;
import com.example.eventplanner.services.QuickRegisterService;
import com.example.eventplanner.services.UserService;
import com.example.eventplanner.services.PriceListService;
import com.example.eventplanner.services.ServiceSolutionService;
import com.example.eventplanner.services.SolutionCategoryService;
import com.example.eventplanner.adapters.datetime.DurationAdapter;
import com.example.eventplanner.adapters.datetime.LocalDateAdapter;
import com.example.eventplanner.adapters.datetime.LocalDateTimeAdapter;
import com.example.eventplanner.adapters.datetime.LocalTimeAdapter;
import com.example.eventplanner.fragments.comment.CommentService;
import com.example.eventplanner.fragments.gallery.GalleryService;
import com.example.eventplanner.fragments.notification.NotificationService;
import com.example.eventplanner.fragments.product.ProductPurchaseService;
import com.example.eventplanner.fragments.report.ReportService;
import com.example.eventplanner.fragments.servicereservation.ServiceReservationService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClientUtils {

    public static final String SERVICE_API_PATH = "http://" + BuildConfig.IP_ADDR + ":8080/api/";
    public static final String BASE_IMAGE_URL = "http://" + BuildConfig.IP_ADDR + ":8080";
    // add adapters for proper LocalDate and LocalTime parsing
    static Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
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

    public static NotificationService notificationService = retrofit.create(NotificationService.class);

    public static CommentService commentService = retrofit.create(CommentService.class);

    public static ReportService reportService = retrofit.create(ReportService.class);

    public static ServiceSolutionService serviceSolutionService = retrofit.create(ServiceSolutionService.class);
    public static PriceListService priceListService = retrofit.create(PriceListService.class);

    public static ServiceReservationService serviceReservationService = retrofit.create(ServiceReservationService.class);
    public static ProductPurchaseService productPurchaseService = retrofit.create(ProductPurchaseService.class);

}
