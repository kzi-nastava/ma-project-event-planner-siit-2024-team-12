package com.example.eventplanner.activities.event;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.eventplanner.R;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Scanner;

public class EventDetailsActivity extends AppCompatActivity {

    private WebView mapWebView;

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


        mapWebView = findViewById(R.id.mapWebView);
        setupWebView();

        ImageView exitBtn = findViewById(R.id.exitBtn);
        exitBtn.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });


        TextView name = findViewById(R.id.name);
        TextView eventType = findViewById(R.id.eventType);
        TextView date = findViewById(R.id.date);
        TextView maxGuests = findViewById(R.id.maxGuests);
        TextView description = findViewById(R.id.description);
        TextView location = findViewById(R.id.location);


        Intent intent = getIntent();
        if (intent != null) {
            name.setText(intent.getStringExtra("name"));
            eventType.setText(intent.getStringExtra("eventType"));
            date.setText(intent.getStringExtra("date"));
            maxGuests.setText(intent.getStringExtra("maxGuests"));
            description.setText(intent.getStringExtra("description"));
            String locationText = intent.getStringExtra("location");

            if (locationText != null && !locationText.trim().isEmpty()) {
                location.setText(locationText);
                loadMap(locationText);
            } else {
                loadMap("Belgrade, Serbia");
            }
        }

    }




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
}