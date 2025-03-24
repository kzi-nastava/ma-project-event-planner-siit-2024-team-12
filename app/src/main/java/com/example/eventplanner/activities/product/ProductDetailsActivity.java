package com.example.eventplanner.activities.product;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.eventplanner.dto.product.GetProductDTO;
import com.example.eventplanner.utils.ClientUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailsActivity extends AppCompatActivity {

    private EditText name, availability, visibility, price, discount, category, description;
    private Button seeEventTypes;



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

    }




    private void loadProductDetails() {
        String auth = ClientUtils.getAuthorization(this);

        Long productId = getIntent().getLongExtra("id", 0);

        Call<GetProductDTO> call = ClientUtils.productService.getProduct(auth, productId);
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
}
