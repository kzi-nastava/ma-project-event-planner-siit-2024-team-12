package com.example.eventplanner.activities.event;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventplanner.dto.event.UpdatedEventDTO;
import com.example.eventplanner.dto.eventtype.GetEventTypeDTO;
import com.example.eventplanner.fragments.eventcreation.AgendaEditFragment;
import com.example.eventplanner.utils.ClientUtils;
import com.example.eventplanner.R;
import com.example.eventplanner.activities.favorites.FavoriteEventsActivity;
import com.example.eventplanner.dto.agenda.CreateActivityDTO;
import com.example.eventplanner.dto.event.EventDetailsDTO;
import com.example.eventplanner.dto.location.CreateLocationDTO;
import com.example.eventplanner.fragments.eventcreation.AgendaDialogFragment;
import com.example.eventplanner.model.Activity;
import com.example.eventplanner.viewmodels.EventEditViewModel;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EventDetailsActivity extends AppCompatActivity {

    private WebView mapWebView;
    private EditText name, date, maxGuests, description, location;
    private String nameTxt, eventTypeTxt, dateTxt, maxGuestsTxt, descriptionTxt, locationText;
    private Long currentEventId;
    private Boolean isFavorite, isEditable = false;
    private ImageView fav, favOutline, exitBtn;
    private EventDetailsDTO eventDetailsDTO = new EventDetailsDTO();
    Button editBtn, seeAgendaButton, pdfBtn;
    private List<CreateActivityDTO> activities = new ArrayList<>();
    private CreateLocationDTO locationDTO = new CreateLocationDTO();
    private List<String> eventTypeNames = new ArrayList<>();
    private Spinner eventTypeSpinner;
    private EventEditViewModel editViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editViewModel = new ViewModelProvider(this).get(EventEditViewModel.class);

        mapWebView = findViewById(R.id.mapWebView);
        setupWebView();

        findTextViews();

        Intent intent = getIntent();
        if (intent != null) {
            populateTextViews(intent);
            getTextValues();
            setUpEventDetailsDTO();
            setUpAgendaBtn(intent);
        }

        loadActiveEventTypes();
        setUpEditBtn();
        setUpExitBtn();
        setUpPdfBtn();
        setUpFavEvents();

    }



    private void loadActiveEventTypes() {
        String auth = ClientUtils.getAuthorization(this);

        Call<ArrayList<GetEventTypeDTO>> call = ClientUtils.eventTypeService.getAllActive(auth);
        call.enqueue(new Callback<ArrayList<GetEventTypeDTO>>() {
            @Override
            public void onResponse(Call<ArrayList<GetEventTypeDTO>> call, Response<ArrayList<GetEventTypeDTO>> response) {
                if (response.isSuccessful()) {
                    List<GetEventTypeDTO> eventTypes = response.body();
                    eventTypeNames.clear();
                    assert eventTypes != null;
                    for (GetEventTypeDTO dto : eventTypes) {
                        eventTypeNames.add(dto.getName());
                    }

                    setUpEventTypeSpinner();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<GetEventTypeDTO>> call, Throwable t) {
                Toast.makeText(EventDetailsActivity.this, "Failed to load active event types!", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void setUpEventTypeSpinner() {
        eventTypeSpinner = findViewById(R.id.eventType);

        if (eventTypeNames.isEmpty()) {
            return;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, eventTypeNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventTypeSpinner.setAdapter(adapter);

        eventTypeSpinner.post(() -> {
            if (eventTypeTxt != null) {
                int selectedIndex = eventTypeNames.indexOf(eventTypeTxt);
                if (selectedIndex != -1) {
                    eventTypeSpinner.setSelection(selectedIndex);
                }
            }
        });


        if (!isEditable) {
            eventTypeSpinner.setEnabled(false);
        }


        eventTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                eventTypeTxt = eventTypeNames.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
    }





    // **********  pdf  **********
    private void generatePdf() throws FileNotFoundException {
        String directoryPath = getExternalFilesDir(null) + "/";
        String fileName = getNextReportFileName(directoryPath);
        String filePath = directoryPath + fileName;

        PdfWriter writer = new PdfWriter(filePath);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        getTextValues();


        try {

            Paragraph title = new Paragraph(nameTxt)
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.LEFT);

            document.add(title);
            document.add(new Paragraph("\n"));


            document.add(new Paragraph("Event type: " + eventTypeTxt).setFontSize(12));
            document.add(new Paragraph("Date: " + dateTxt).setFontSize(12));
            document.add(new Paragraph("Max guests: " + maxGuestsTxt).setFontSize(12));
            document.add(new Paragraph("Description: " + descriptionTxt).setFontSize(12));
            document.add(new Paragraph("Location: " + locationText).setFontSize(12));
            document.add(new Paragraph("\nAgenda:\n"));


            // add detailed table
            Table table = new Table(4);
            table.setWidth(UnitValue.createPercentValue(100));

            // set header
            DeviceRgb headerColor = new DeviceRgb(44, 62, 80);
            DeviceRgb white = new DeviceRgb(255, 255, 255);
            table.addCell(new Cell().add(new Paragraph("Time")).setBackgroundColor(headerColor).setFontColor(white));
            table.addCell(new Cell().add(new Paragraph("Activity name")).setBackgroundColor(headerColor).setFontColor(white));
            table.addCell(new Cell().add(new Paragraph("Description")).setBackgroundColor(headerColor).setFontColor(white));
            table.addCell(new Cell().add(new Paragraph("Location")).setBackgroundColor(headerColor).setFontColor(white));

            DeviceRgb firstColumnColor = new DeviceRgb(44, 62, 80);

            // populate table
            for (CreateActivityDTO activityDTO : activities) {
                table.addCell(new Cell().add(new Paragraph(activityDTO.getTime())).setBackgroundColor(firstColumnColor).setFontColor(white));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(activityDTO.getName()))));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(activityDTO.getDescription()))));
                table.addCell(new Cell().add(new Paragraph(activityDTO.getLocation())));
            }

            document.add(table);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            document.close();
        }

        // open newly created pdf file
        openGeneratedPDF(filePath);

    }


    private String getNextReportFileName(String directoryPath) {
        File directory = new File(directoryPath);
        File[] files = directory.listFiles((dir, name) -> name.startsWith("Details_") && name.endsWith(".pdf"));

        int highestNumber = 0;

        if (files != null) {
            for (File file : files) {
                String fileName = file.getName();
                try {
                    String numberPart = fileName.replaceAll("[^0-9]", "");
                    int fileNumber = Integer.parseInt(numberPart);
                    highestNumber = Math.max(highestNumber, fileNumber);
                } catch (NumberFormatException e) {
                }
            }
        }

        int nextReportNumber = highestNumber + 1;
        return "Details_" + nextReportNumber + ".pdf";
    }

    private void openGeneratedPDF(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            Uri uri = FileProvider.getUriForFile(this, "com.example.eventplanner.provider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } else {
            Toast.makeText(this, "PDF not found!", Toast.LENGTH_LONG).show();
        }
    }




    // **********  favorites  **********
    private void addToFavorites() {
        String auth = ClientUtils.getAuthorization(this);
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("email", "a");

        if (currentEventId == null) {
            return;
        }

        Call<ResponseBody> call = ClientUtils.userService.addToFavorites(auth, userEmail, currentEventId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    fav.setVisibility(View.VISIBLE);
                    Toast.makeText(EventDetailsActivity.this, "Added event to favorites!", Toast.LENGTH_SHORT).show();

                }
                else {
                    Toast.makeText(EventDetailsActivity.this, "Unsuccessful", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(EventDetailsActivity.this, "Failed to add event to favorites!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void checkIfFavorite() {
        isFavorite = false;
        String auth = ClientUtils.getAuthorization(this);

        SharedPreferences pref = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String email = pref.getString("email", "a");

        Call<Boolean> call = ClientUtils.userService.isEventFavorite(auth, email, currentEventId);
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
                Toast.makeText(EventDetailsActivity.this, "Failed to check if favorite!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void removeFromFavorites() {
        String auth = ClientUtils.getAuthorization(this);

        SharedPreferences pref = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String email = pref.getString("email", "a");

        if (currentEventId == null) {
            return;
        }

        Call<Void> call = ClientUtils.userService.removeFromFavorites(auth, email, currentEventId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    fav.setVisibility(View.GONE);
                    favOutline.setVisibility(View.VISIBLE);
                    Toast.makeText(EventDetailsActivity.this, "Removed event from favorites!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(EventDetailsActivity.this, "Unsuccessful", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EventDetailsActivity.this, "Failed to remove event from favorites!", Toast.LENGTH_SHORT).show();
            }
        });
    }



    // **********  leaflet map  **********
    private void setupWebView() {
        WebSettings webSettings = mapWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mapWebView.setWebViewClient(new WebViewClient());
    }

    private void loadMap(String location) {
        String leafletHTML = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>\n" +
                "    <link rel='stylesheet' href='https://unpkg.com/leaflet@1.7.1/dist/leaflet.css'/>\n" +
                "    <script src='https://unpkg.com/leaflet@1.7.1/dist/leaflet.js'></script>\n" +
                "</head>\n" +
                "<body>\n" +
                "<div id='map' style='width: 100%; height: 600px;'></div>\n" +
                "<script>\n" +
                "    var map = L.map('map').setView([44.7866, 20.4489], 15);\n" +  // Default: Belgrade
                "    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {\n" +
                "        attribution: '&copy; OpenStreetMap contributors'\n" +
                "    }).addTo(map);\n" +

                "    var redIcon = L.icon({\n" +
                "        iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-red.png',\n" +
                "        iconSize: [25, 41],\n" +
                "        iconAnchor: [12, 41],\n" +
                "        popupAnchor: [1, -34],\n" +
                "        shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',\n" +
                "        shadowSize: [41, 41]\n" +
                "    });\n" +

                "    function updateLocation(lat, lon, name) {\n" +
                "        map.setView([lat, lon], 15);\n" +
                "        L.marker([lat, lon], { icon: redIcon }).addTo(map).bindPopup(name).openPopup();\n" +
                "    }\n" +
                "</script>\n" +
                "</body>\n" +
                "</html>";

        mapWebView.loadData(leafletHTML, "text/html", "UTF-8");

        getCoordinatesFromOSM(location);
    }

    private void getCoordinatesFromOSM(String location) {
        new Thread(() -> {
            try {
                String urlStr = "https://nominatim.openstreetmap.org/search?format=json&q=" + URLEncoder.encode(location, "UTF-8");
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");

                Scanner scanner = new Scanner(conn.getInputStream());
                String response = scanner.useDelimiter("\\A").next();
                scanner.close();

                JSONArray results = new JSONArray(response);
                if (results.length() > 0) {
                    JSONObject firstResult = results.getJSONObject(0);
                    double lat = firstResult.getDouble("lat");
                    double lon = firstResult.getDouble("lon");

                    runOnUiThread(() -> mapWebView.loadUrl("javascript:updateLocation(" + lat + ", " + lon + ", '" + "Event location" + "')"));
                }
                else {
                    runOnUiThread(() -> {
                        Toast.makeText(EventDetailsActivity.this, "Unknown address!", Toast.LENGTH_LONG).show();
                    });
                }
            } catch (Exception e) {
                Toast.makeText(EventDetailsActivity.this, "Unknown address!", Toast.LENGTH_SHORT).show();
                Log.e("MapError", "Error fetching location: " + e.getMessage());
            }
        }).start();
    }



    // **********  general set up  **********

    private void getTextValues() {
        nameTxt = name.getText().toString();
        dateTxt = date.getText().toString();
        maxGuestsTxt = maxGuests.getText().toString();
        descriptionTxt = description.getText().toString();
        locationText = location.getText().toString();

        String[] parts = locationText.split(",");
        locationDTO = new CreateLocationDTO(" ", parts[0], parts[1], parts[2]);  // venue name is " "
    }

    private void setUpEventDetailsDTO() {
        getTextValues();

        eventDetailsDTO.setId(currentEventId);
        eventDetailsDTO.setName(nameTxt);
        eventDetailsDTO.setEventType(eventTypeTxt);
        eventDetailsDTO.setDate(LocalDate.parse(dateTxt));
        eventDetailsDTO.setMaxGuests(maxGuestsTxt);
        eventDetailsDTO.setDescription(descriptionTxt);
        eventDetailsDTO.setActivities(activities);
        eventDetailsDTO.setLocation(locationDTO);
    }




    private void setUpUpdateEventDetailsDTO() {
        getTextValues();

        eventDetailsDTO.setId(currentEventId);
        eventDetailsDTO.setName(nameTxt);
        eventDetailsDTO.setEventType(eventTypeTxt);
        eventDetailsDTO.setDate(LocalDate.parse(dateTxt));
        eventDetailsDTO.setMaxGuests(maxGuestsTxt);
        eventDetailsDTO.setDescription(descriptionTxt);
        eventDetailsDTO.setLocation(locationDTO);
    }


    private void populateTextViews(Intent intent) {
        currentEventId = intent.getLongExtra("id", 0L);
        eventTypeTxt = intent.getStringExtra("eventType");
        name.setText(intent.getStringExtra("name"));
        date.setText(intent.getStringExtra("date"));
        maxGuests.setText(intent.getStringExtra("maxGuests"));
        description.setText(intent.getStringExtra("description"));
        activities = (List<CreateActivityDTO>) intent.getSerializableExtra("activities");

        locationText = intent.getStringExtra("location");
        if (locationText != null && !locationText.trim().isEmpty()) {
            location.setText(locationText);
            loadMap(locationText);

            String[] parts = locationText.split(",");
            locationDTO = new CreateLocationDTO(" ", parts[0], parts[1], parts[2]);  // venue name is " "
            eventDetailsDTO.setLocation(locationDTO);
        } else {
            loadMap("Belgrade, Serbia");
        }
    }


    private void setEditViewModel() {
        editViewModel.updateEventAttributes("id", currentEventId.toString());

        for (CreateActivityDTO dto : eventDetailsDTO.getActivities()) {
            editViewModel.updateAgenda(dto);
        }
    }



    private void setUpAgendaBtn(Intent intent) {
        seeAgendaButton = findViewById(R.id.seeAgenda);
        List<Activity> adapterActivities = new ArrayList<>();

        for (CreateActivityDTO dto : activities) {
            Activity activity = new Activity(dto.getTime(), dto.getName(), dto.getDescription(), dto.getLocation());
            adapterActivities.add(activity);
        }

        seeAgendaButton.setOnClickListener(v -> {
            if (isEditable) {
                AgendaEditFragment editFragment = AgendaEditFragment.newInstance(eventDetailsDTO);
                editFragment.show(getSupportFragmentManager(), "AgendaEdit");
            }
            else {
                AgendaDialogFragment agendaDialog = new AgendaDialogFragment(adapterActivities);
                agendaDialog.show(getSupportFragmentManager(), "AgendaDialog");
            }
        });
    }



    private void setUpEditBtn() {
        editBtn = findViewById(R.id.editBtn);

        editBtn.setOnClickListener(v -> {
            if (isEditable) {
                updateEvent();
            }
            else {
                enterEditMode();
            }
        });

    }

    private void updateEvent() {
        String auth = ClientUtils.getAuthorization(this);
        setUpUpdateEventDetailsDTO();

        Set<CreateActivityDTO> unique = new HashSet<>(editViewModel.getDto().getValue().getActivities());
        eventDetailsDTO.setActivities(new ArrayList<>(unique));


        Call<UpdatedEventDTO> call = ClientUtils.eventService.updateEvent(auth, currentEventId, eventDetailsDTO);
        call.enqueue(new Callback<UpdatedEventDTO>() {
            @Override
            public void onResponse(Call<UpdatedEventDTO> call, Response<UpdatedEventDTO> response) {
                if (response.isSuccessful()) {
                    UpdatedEventDTO dto = response.body();
                    eventDetailsDTO.setName(dto.getName());
                    eventDetailsDTO.setEventType(dto.getEventType());
                    eventDetailsDTO.setDate(dto.getDate());
                    eventDetailsDTO.setMaxGuests(dto.getMaxGuests());
                    eventDetailsDTO.setDescription(dto.getDescription());
                    eventDetailsDTO.setLocation(dto.getLocation());

                    // refresh agenda ui
                    activities.clear();
                    activities.addAll(dto.getActivities());
                    setUpAgendaBtn(getIntent());

                    loadMap(dto.getLocation().getAddress() + ", " + dto.getLocation().getCity() + ", " + dto.getLocation().getCountry());
                    isEditable = false;
                    exitEditMode();
                }
            }

            @Override
            public void onFailure(Call<UpdatedEventDTO> call, Throwable t) {

            }
        });
    }

    private void setUpPdfBtn() {
        pdfBtn = findViewById(R.id.pdfBtn);

        pdfBtn.setOnClickListener(v -> {
            try {
                generatePdf();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }


    private void setUpFavEvents() {
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



    private void setUpExitBtn() {
        exitBtn = findViewById(R.id.exitBtn);

        exitBtn.setOnClickListener(v -> {
            Intent intent = new Intent(EventDetailsActivity.this, FavoriteEventsActivity.class);
            startActivity(intent);
        });
    }

    private void enterEditMode() {
        editBtn.setText(getString(R.string.save));

        setEditViewModel();

        name.setBackgroundResource(R.drawable.display_field);
        name.setFocusableInTouchMode(true);

        date.setFocusableInTouchMode(true);
        maxGuests.setFocusableInTouchMode(true);
        description.setFocusableInTouchMode(true);
        location.setFocusableInTouchMode(true);

        eventTypeSpinner.setEnabled(true);

        isEditable = true;
    }


    private void exitEditMode() {
        editBtn.setText(getString(R.string.edit));

        name.setBackgroundColor(Color.TRANSPARENT);
        name.setFocusableInTouchMode(false);

        date.setFocusableInTouchMode(false);
        maxGuests.setFocusableInTouchMode(false);
        description.setFocusableInTouchMode(false);
        location.setFocusableInTouchMode(false);

        View currentFocusView = getCurrentFocus();
        if (currentFocusView instanceof EditText) {
            currentFocusView.clearFocus();
        }

        eventTypeSpinner.setEnabled(false);

        isEditable = false;
    }


    private void findTextViews() {
        name = findViewById(R.id.name);
        name.setBackgroundColor(Color.TRANSPARENT);

        date = findViewById(R.id.date);
        maxGuests = findViewById(R.id.maxGuests);
        description = findViewById(R.id.description);
        location = findViewById(R.id.location);
    }

}