package com.example.eventplanner.activities.product;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.eventplanner.R;
import com.example.eventplanner.activities.event.EventDetailsActivity;
import com.example.eventplanner.activities.favorites.FavoriteEventsActivity;
import com.example.eventplanner.activities.favorites.FavoriteProductsActivity;
import com.example.eventplanner.dto.product.GetProductDTO;
import com.example.eventplanner.utils.ClientUtils;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailsActivity extends AppCompatActivity {

    private EditText name, availability, visibility, price, discount, category, description;
    private Button seeEventTypes;
    private ImageView fav, favOutline, exitBtn;
    private Long currentProductId;
    private Boolean isFavorite;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findTextViews();

        loadProductDetails();

        seeEventTypes = findViewById(R.id.seeEventTypes);

        setUpFavProducts();
        setUpExitBtn();

    }




    private void loadProductDetails() {
        String auth = ClientUtils.getAuthorization(this);

        currentProductId = getIntent().getLongExtra("id", 0);


        Call<GetProductDTO> call = ClientUtils.productService.getProduct(auth, currentProductId);
        call.enqueue(new Callback<GetProductDTO>() {
            @Override
            public void onResponse(Call<GetProductDTO> call, Response<GetProductDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GetProductDTO productDTO = response.body();
                    populateTextViews(productDTO);
                    setUpEventTypes(productDTO);
                }
                else {
                    Toast.makeText(ProductDetailsActivity.this, "Error loading product details!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetProductDTO> call, Throwable t) {
                Toast.makeText(ProductDetailsActivity.this, "Failed to load product details!", Toast.LENGTH_SHORT).show();

            }
        });
    }



    private void findTextViews() {
        name = findViewById(R.id.name);
        availability = findViewById(R.id.availability);
        visibility = findViewById(R.id.visibility);
        price = findViewById(R.id.price);
        discount = findViewById(R.id.discount);
        category = findViewById(R.id.category);
        description = findViewById(R.id.description);
    }



    private void populateTextViews(GetProductDTO product) {
        name.setText(product.getName());
        availability.setText(product.getIsAvailable() ? "Available" : "Unavailable");
        visibility.setText(product.getIsVisible() ? "Visible" : "Invisible");
        price.setText(String.valueOf(product.getPrice()));
        discount.setText(String.valueOf(product.getDiscount()));
        category.setText(product.getCategoryName());
        description.setText(product.getDescription());
    }



    private void setUpEventTypes(GetProductDTO product) {
        ArrayList<String> eventTypes =  new ArrayList<>(product.getEventTypeNames());
        String[] eventTypesArray = new String[eventTypes.size()];

        eventTypes.toArray(eventTypesArray);

        boolean[] selectedEventTypes = new boolean[eventTypesArray.length];

        seeEventTypes.setOnClickListener(v -> {
            for (int i = 0; i < selectedEventTypes.length; i++) {
                selectedEventTypes[i] = true;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Event types :");

            builder.setMultiChoiceItems(eventTypesArray, selectedEventTypes, (dialog, which, isChecked) -> {
                // do nothing because checkboxes should be disabled in product details display
            });


            builder.setPositiveButton("OK", (dialog, which) -> {
                StringBuilder selected = new StringBuilder();

                for (int i = 0; i < eventTypesArray.length; i++) {
                    if (selectedEventTypes[i]) {
                        if (selected.length() > 0) selected.append(", ");
                        selected.append(eventTypesArray[i]);
                    }
                }

                seeEventTypes.setText(selected.length() > 0 ? selected.toString() : "See event types");
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();


            // disable interaction with checkboxes
            ListView listView = dialog.getListView();
            listView.setEnabled(false);

            dialog.show();

        });
    }




    private void setUpFavProducts() {
        fav = findViewById(R.id.fav);
        favOutline = findViewById(R.id.favOutline);

        checkIfFavorite();

        favOutline.setOnClickListener(v -> {
            addToFavorites();
        });

        fav.setOnClickListener(v -> {
            removeFromFavorites();
        });

    }




    // **********  favorites  **********
    private void addToFavorites() {
        String auth = ClientUtils.getAuthorization(this);
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("email", "a");

        if (currentProductId == null) {
            return;
        }

        Call<ResponseBody> call = ClientUtils.userService.addFavoriteProduct(auth, userEmail, currentProductId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    fav.setVisibility(View.VISIBLE);
                    Toast.makeText(ProductDetailsActivity.this, "Added product to favorites!", Toast.LENGTH_SHORT).show();

                }
                else {
                    Toast.makeText(ProductDetailsActivity.this, "Unsuccessful", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ProductDetailsActivity.this, "Failed to add product to favorites!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void checkIfFavorite() {
        isFavorite = false;
        String auth = ClientUtils.getAuthorization(this);

        SharedPreferences pref = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String email = pref.getString("email", "a");

        Call<Boolean> call = ClientUtils.userService.isProductFavorite(auth, email, currentProductId);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    isFavorite = response.body();

                    if (Boolean.TRUE.equals(isFavorite)) {
                        fav.setVisibility(View.VISIBLE);
                    }
                    else {
                        favOutline.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(ProductDetailsActivity.this, "Failed to check if favorite!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void removeFromFavorites() {
        String auth = ClientUtils.getAuthorization(this);

        SharedPreferences pref = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String email = pref.getString("email", "a");

        if (currentProductId == null) {
            return;
        }

        Call<Void> call = ClientUtils.userService.removeFavoriteProduct(auth, email, currentProductId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    fav.setVisibility(View.GONE);
                    favOutline.setVisibility(View.VISIBLE);
                    Toast.makeText(ProductDetailsActivity.this, "Removed product from favorites!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(ProductDetailsActivity.this, "Unsuccessful", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ProductDetailsActivity.this, "Failed to remove product from favorites!", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void setUpExitBtn() {
        exitBtn = findViewById(R.id.exitBtn);

        exitBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ProductDetailsActivity.this, FavoriteProductsActivity.class);
            startActivity(intent);
        });
    }

}
