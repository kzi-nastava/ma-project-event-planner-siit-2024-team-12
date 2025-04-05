package com.example.eventplanner.activities.product;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.eventplanner.R;
import com.example.eventplanner.UserRole;
import com.example.eventplanner.activities.eventtype.EventTypeEditActivity;
import com.example.eventplanner.activities.favorites.FavoriteProductsActivity;
import com.example.eventplanner.dto.business.GetBusinessDTO;
import com.example.eventplanner.dto.eventtype.GetEventTypeDTO;
import com.example.eventplanner.dto.product.GetProductDTO;
import com.example.eventplanner.dto.product.UpdateProductDTO;
import com.example.eventplanner.dto.product.UpdatedProductDTO;
import com.example.eventplanner.utils.ClientUtils;
import com.example.eventplanner.utils.ValidationUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailsActivity extends AppCompatActivity {

    private EditText name, availability, visibility, price, discount, category, description;
    private Button seeEventTypes, editBtn, deleteBtn, chatBtn;
    private ImageView fav, favOutline, exitBtn, shoppingCart;
    private Long currentProductId;
    private Boolean isFavorite, isEditable = false;
    private RadioGroup availabilityGroup, visibilityGroup;
    private RadioButton availableBtn, unavailableBtn, visibleBtn, invisibleBtn;
    private List<String> selectedEventTypes = new ArrayList<>();
    private String currentCompanyEmail, loadedCompanyEmail;
    private TextView moreInfo, visible;




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

        findFields();

        loadProductDetails();

        setUpFavProducts();
        setUpExitBtn();
        setUpEditBtn();

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
                    loadedCompanyEmail = productDTO.getCompanyEmail();
                    Log.d("iz load ", loadedCompanyEmail);
                    populateTextViews(productDTO);
                    setUpEventTypes(productDTO);
                    displayBasedOnRole();
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



    private void displayBasedOnRole() {
        // currently logged in user
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String role = prefs.getString("userRole", "");


        if (role.equals(UserRole.ROLE_ORGANIZER.toString())) {
            displayOrganizersView();
        }
        else if (role.equals(UserRole.ROLE_PROVIDER.toString())) {
            loadCurrentCompany();
        }

    }


    private void displayOrganizersView() {
        editBtn.setVisibility(View.GONE);
        visibility.setVisibility(View.GONE);
        visible.setVisibility(View.GONE);
        moreInfo.setVisibility(View.VISIBLE);
        chatBtn.setVisibility(View.VISIBLE);
        shoppingCart.setVisibility(View.VISIBLE);
    }



    private void loadCurrentCompany() {
        String auth = ClientUtils.getAuthorization(this);

        Call<GetBusinessDTO> call = ClientUtils.businessService.getBusinessForCurrentUser(auth);
        call.enqueue(new Callback<GetBusinessDTO>() {
            @Override
            public void onResponse(Call<GetBusinessDTO> call, Response<GetBusinessDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GetBusinessDTO dto = response.body();
                    currentCompanyEmail = dto.getCompanyEmail();

                    checkEditPermission();
                }
                else {
                    Toast.makeText(ProductDetailsActivity.this, "Error loading current business!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetBusinessDTO> call, Throwable t) {
                Toast.makeText(ProductDetailsActivity.this, "Failed to load current business!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void checkEditPermission() {
        if (!currentCompanyEmail.equals(loadedCompanyEmail)) {
            editBtn.setVisibility(View.GONE);
        }
    }
    



    // **********  general set up  **********

    private void findFields() {
        name = findViewById(R.id.name);
        availability = findViewById(R.id.availability);
        visibility = findViewById(R.id.visibility);
        price = findViewById(R.id.price);
        discount = findViewById(R.id.discount);
        category = findViewById(R.id.category);
        description = findViewById(R.id.description);

        seeEventTypes = findViewById(R.id.seeEventTypes);

        availabilityGroup = findViewById(R.id.availabilityGroup);
        visibilityGroup = findViewById(R.id.visibilityGroup);

        availableBtn = findViewById(R.id.availableBtn);
        unavailableBtn = findViewById(R.id.unavailableBtn);
        visibleBtn = findViewById(R.id.visibleBtn);
        invisibleBtn = findViewById(R.id.invisibleBtn);

        deleteBtn = findViewById(R.id.deleteBtn);

        moreInfo = findViewById(R.id.more_info);
        chatBtn = findViewById(R.id.chatBtn);
        shoppingCart = findViewById(R.id.shop);
        visible = findViewById(R.id.visible);

    }



    private void populateTextViews(GetProductDTO product) {
        name.setText(product.getName());
        availability.setText(product.getIsAvailable() ? getString(R.string.available) : getString(R.string.unavailable));
        visibility.setText(product.getIsVisible() ? getString(R.string.visible) : getString(R.string.invisible));
        price.setText(String.valueOf(product.getPrice()));
        discount.setText(String.valueOf(product.getDiscount()));
        category.setText(product.getCategoryName());
        description.setText(product.getDescription());
    }



    private void setUpEventTypes(GetProductDTO product) {
        ArrayList<String> eventTypes =  new ArrayList<>(product.getEventTypeNames());
        selectedEventTypes = new ArrayList<>(product.getEventTypeNames());

        String[] eventTypesArray = new String[eventTypes.size()];
        eventTypes.toArray(eventTypesArray);

        seeEventTypes.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Event types :");

            boolean[] selected = new boolean[eventTypesArray.length];
            for (int i = 0; i < eventTypesArray.length; i++) {
                selected[i] = true;
            }

            builder.setMultiChoiceItems(eventTypesArray, selected, (dialog, which, isChecked) -> {
                // do nothing because checkboxes should be disabled in product details display
            });

            builder.setPositiveButton("OK", (dialog, which) -> {
                StringBuilder selectedText = new StringBuilder();
                for (int i = 0; i < eventTypesArray.length; i++) {
                    if (selected[i]) {
                        if (selectedText.length() > 0) selectedText.append(", ");
                        selectedText.append(eventTypesArray[i]);
                    }
                }
                seeEventTypes.setText(selectedText.length() > 0 ? selectedText.toString() : "See event types");
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();

            // disable interaction with checkboxes
            ListView listView = dialog.getListView();
            listView.setEnabled(false);

            dialog.show();
        });
    }



    private void setUpExitBtn() {
        exitBtn = findViewById(R.id.exitBtn);

        exitBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ProductDetailsActivity.this, FavoriteProductsActivity.class);
            startActivity(intent);
        });
    }



    // **********  favorites  **********

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



    // **********  product edit  **********

    private void setUpEditBtn() {
        editBtn = findViewById(R.id.editBtn);

        editBtn.setOnClickListener(v -> {
            if (isEditable) {
                getUpdateProductDTO();
            }
            else {
                enterEditMode();
            }
        });

    }



    private void enterEditMode() {
        editBtn.setText(getString(R.string.save));

        name.setBackgroundResource(R.drawable.display_field);
        name.setFocusableInTouchMode(true);

        price.setFocusableInTouchMode(true);
        discount.setFocusableInTouchMode(true);
        description.setFocusableInTouchMode(true);

        availability.setVisibility(View.GONE);
        visibility.setVisibility(View.GONE);

        availabilityGroup.setVisibility(View.VISIBLE);
        visibilityGroup.setVisibility(View.VISIBLE);

        setRadioButtons();

        seeEventTypes.setOnClickListener(v -> {
            loadActiveEventTypes(seeEventTypes, new ArrayList<>(selectedEventTypes));
        });

        deleteBtn.setVisibility(View.VISIBLE);
        deleteBtn.setOnClickListener(v -> {
            confirmProductDeletion();
        });

        isEditable = true;
    }


    private void setRadioButtons() {
        // availability
        String available = availability.getText().toString();

        if (available.equals(getString(R.string.available))) {
            availableBtn = findViewById(R.id.availableBtn);
            availableBtn.setChecked(true);
        }
        else {
            unavailableBtn.setChecked(true);
        }


        // visibility
        String visible = visibility.getText().toString();

        if (visible.equals(getString(R.string.visible))) {
            visibleBtn.setChecked(true);
        }
        else {
            invisibleBtn.setChecked(true);
        }

    }

    private void exitEditMode() {
        editBtn.setText(getString(R.string.edit));

        name.setBackgroundColor(Color.TRANSPARENT);
        name.setFocusableInTouchMode(false);

        price.setFocusableInTouchMode(false);
        discount.setFocusableInTouchMode(false);
        description.setFocusableInTouchMode(false);

        availabilityGroup.setVisibility(View.GONE);
        visibilityGroup.setVisibility(View.GONE);

        availability.setVisibility(View.VISIBLE);
        visibility.setVisibility(View.VISIBLE);

        View currentFocusView = getCurrentFocus();
        if (currentFocusView instanceof EditText) {
            currentFocusView.clearFocus();
        }

        isEditable = false;
    }



    private void loadActiveEventTypes(Button seeEventTypes, ArrayList<String> selectedEventTypes) {
        String auth = ClientUtils.getAuthorization(this);

        Call<ArrayList<GetEventTypeDTO>> call = ClientUtils.eventTypeService.getAllActive(auth);
        call.enqueue(new Callback<ArrayList<GetEventTypeDTO>>() {
            @Override
            public void onResponse(Call<ArrayList<GetEventTypeDTO>> call, Response<ArrayList<GetEventTypeDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ArrayList<GetEventTypeDTO> eventTypeDTOS = response.body();
                    List<String> eventTypeNames = new ArrayList<>();

                    for (GetEventTypeDTO dto : eventTypeDTOS) {
                        eventTypeNames.add(dto.getName());
                    }

                    if (!eventTypeNames.isEmpty()) {
                        showMultiChoiceDialog(eventTypeNames, selectedEventTypes, seeEventTypes);
                    }
                    else {
                        Toast.makeText(ProductDetailsActivity.this, "No categories available.", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(ProductDetailsActivity.this, "Failed to load categories.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<GetEventTypeDTO>> call, Throwable t) {
                Toast.makeText(ProductDetailsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }




    private void showMultiChoiceDialog(List<String> activeEventTypes, List<String> selectedEventTypeNames, Button seeEventTypes) {
        String[] eventTypesArray = activeEventTypes.toArray(new String[0]);
        boolean[] selectedTypes = new boolean[eventTypesArray.length];

        // preselect already suggested categories
        for (int i = 0; i < eventTypesArray.length; i++) {
            selectedTypes[i] = selectedEventTypeNames.contains(eventTypesArray[i]);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selected event types");

        builder.setMultiChoiceItems(eventTypesArray, selectedTypes, (dialog, which, isChecked) -> {
            selectedTypes[which] = isChecked;
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
            // save new choice of suggested categories
            selectedEventTypeNames.clear();
            for (int i = 0; i < eventTypesArray.length; i++) {
                if (selectedTypes[i]) {
                    selectedEventTypeNames.add(eventTypesArray[i]);
                }
            }

            selectedEventTypes.clear();
            selectedEventTypes = selectedEventTypeNames;

            // display checked categories on the button
            StringBuilder selected = new StringBuilder();
            for (String category : selectedEventTypeNames) {
                if (selected.length() > 0) selected.append(", ");
                selected.append(category);
            }
            seeEventTypes.setText(selected.length() > 0 ? selected.toString() : "Selected event types");

        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }



    private boolean validateInputFields() {
        if (!ValidationUtils.isFieldValid(name, "Name is required!")) return false;
        if (!ValidationUtils.isFieldValid(price, "Price is required!")) return false;
        if (!ValidationUtils.isDecimalNumber(price, "Enter a number!", "Negative number")) return false;
        if (!ValidationUtils.isFieldValid(discount, "Discount is required!")) return false;
        if (!ValidationUtils.isDecimalNumber(discount, "Enter a number!", "Negative number!")) return false;
        if (!ValidationUtils.isFieldValid(description, "Description is required!")) return false;

        if (selectedEventTypes.isEmpty()) {
            Toast.makeText(ProductDetailsActivity.this, "Select event type!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }



    private void getUpdateProductDTO() {
        if (!validateInputFields()) {
            return;
        }

        UpdateProductDTO dto = new UpdateProductDTO();

        dto.setName(name.getText().toString());
        dto.setIsAvailable(availableBtn.isChecked());
        dto.setIsVisible(visibleBtn.isChecked());
        dto.setPrice(Double.parseDouble(price.getText().toString()));
        dto.setDiscount(Double.parseDouble(discount.getText().toString()));
        dto.setEventTypeNames(selectedEventTypes);
        dto.setDescription(description.getText().toString());

        updateProduct(dto);

    }

    private void updateProduct(UpdateProductDTO updateProductDTO) {
        String auth = ClientUtils.getAuthorization(this);

        Call<UpdatedProductDTO> call = ClientUtils.productService.updateProduct(auth, updateProductDTO, currentProductId);
        call.enqueue(new Callback<UpdatedProductDTO>() {
            @Override
            public void onResponse(Call<UpdatedProductDTO> call, Response<UpdatedProductDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UpdatedProductDTO dto = response.body();
                    setUpUpdatedForm(dto);

                    isEditable = false;
                    exitEditMode();
                }
            }

            @Override
            public void onFailure(Call<UpdatedProductDTO> call, Throwable t) {

            }
        });
    }



    private void setUpUpdatedForm(UpdatedProductDTO dto) {
        name.setText(dto.getName());
        availability.setText(dto.getIsAvailable() ? getString(R.string.available) : getString(R.string.unavailable));
        visibility.setText(dto.getIsVisible() ? getString(R.string.visible) : getString(R.string.invisible));
        price.setText(String.valueOf(dto.getPrice()));
        discount.setText(String.valueOf(dto.getDiscount()));
        selectedEventTypes = dto.getEventTypeNames();
        description.setText(dto.getDescription());

    }


    private void confirmProductDeletion() {
        // confirmation dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(ProductDetailsActivity.this);
        builder.setTitle("Delete " + name.getText().toString() + "?");
        builder.setMessage("Are you sure you want to delete this product?");

        builder.setPositiveButton("Yes", (dialog, which) -> {
            deleteProduct();
        });

        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void deleteProduct() {
        String auth = ClientUtils.getAuthorization(this);

        Call<ResponseBody> call = ClientUtils.productService.deleteProduct(auth, currentProductId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProductDetailsActivity.this, "Successfully deleted product!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ProductDetailsActivity.this, ProvidedProductsActivity.class);
                    startActivity(intent);

                }
                else {
                    Toast.makeText(ProductDetailsActivity.this, "Error deleting product!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ProductDetailsActivity.this, "Failed to delete product!", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
