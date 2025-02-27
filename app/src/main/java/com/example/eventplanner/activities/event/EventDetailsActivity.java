package com.example.eventplanner.activities.event;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

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
import java.util.Scanner;

public class EventDetailsActivity extends AppCompatActivity {

    private MapView mapView;

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
                initMap(locationText);
            } else {
                initMap("Beograd, Serbia");
            }
        }

    }


    private void initMap(String location) {
        Configuration.getInstance().setUserAgentValue(getApplicationContext().getPackageName());

        mapView = findViewById(R.id.map);
        mapView.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        GeoPoint geoPoint = getCoordinatesFromOSM(location);
        if (geoPoint == null) {
            geoPoint = new GeoPoint(44.7866, 20.4489);  // default location (Belgrade)
        }

        mapView.getController().setZoom(15.0);
        mapView.getController().setCenter(geoPoint);

        Marker marker = new Marker(mapView);
        marker.setPosition(geoPoint);
        marker.setTitle("Event location");
        mapView.getOverlays().add(marker);
        mapView.invalidate();
    }


    private GeoPoint getCoordinatesFromOSM(String locationName) {
        try {
            String urlStr = "https://nominatim.openstreetmap.org/search?format=json&q=" + locationName.replace(" ", "%20");

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
                return new GeoPoint(lat, lon);
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}