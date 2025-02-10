package com.example.eventplanner.activities.charts;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.eventplanner.ClientUtils;
import com.example.eventplanner.R;
import com.example.eventplanner.dto.charts.EventAttendanceDTO;
import com.example.eventplanner.dto.charts.EventRatingsDTO;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RatingsChart extends AppCompatActivity {
    private BarChart barChart;
    private ArrayList<String> eventNames = new ArrayList<>();
    private ArrayList<EventRatingsDTO> eventList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratings_chart);

        barChart = findViewById(R.id.barChart);

        loadEventRatings();

        Button pdfBtn = findViewById(R.id.pdfBtn);
        pdfBtn.setOnClickListener(v -> {
            if (!eventList.isEmpty()) {
                try {
                    generatePDF(eventList);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error generating PDF!", Toast.LENGTH_SHORT).show();
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            } else {
                Toast.makeText(this, "Loading data, please wait...", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void closeForm(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }


    private void loadEventRatings() {
        String auth = ClientUtils.getAuthorization(this);
        Call<ArrayList<EventRatingsDTO>> call = ClientUtils.chartService.getEventRatings(auth);

        call.enqueue(new Callback<ArrayList<EventRatingsDTO>>() {
            @Override
            public void onResponse(Call<ArrayList<EventRatingsDTO>> call, Response<ArrayList<EventRatingsDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    eventList = response.body();
                    setBarChart(eventList);
                } else {
                    Toast.makeText(RatingsChart.this, "No data available!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<EventRatingsDTO>> call, Throwable t) {
                Toast.makeText(RatingsChart.this, "Failed to load event ratings!", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void setBarChart(ArrayList<EventRatingsDTO> eventList) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        eventNames.clear();

        int limit = Math.min(eventList.size(), 15);

        for (int i = 0; i < limit; i++) {
            EventRatingsDTO event = eventList.get(i);
            eventNames.add(event.getEventName());

            float avgRating = event.getAverageRating().floatValue();
            entries.add(new BarEntry(i, avgRating));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Average rating");
        dataSet.setColor(Color.parseColor("#c899c9"));
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.8f);

        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.setFitBars(true);
        barChart.invalidate();

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(eventNames));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(270f);
        xAxis.setTextSize(12f);
        xAxis.setGranularityEnabled(true);
        xAxis.setAvoidFirstLastClipping(false);
        xAxis.setLabelCount(limit);

        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(5f);
        yAxis.setLabelCount(11, true);
        yAxis.setGranularity(0.5f);
        yAxis.setGranularityEnabled(true);

        barChart.getAxisRight().setEnabled(false);
        barChart.setExtraBottomOffset(250f);
        barChart.setExtraTopOffset(30f);
        barChart.setExtraLeftOffset(12f);

        Legend legend = barChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setTextSize(12f);
        legend.setFormSize(16f);
        legend.setXEntrySpace(20f);
        legend.setXOffset(-25f);
    }



    private void generatePDF(ArrayList<EventRatingsDTO> eventList) throws FileNotFoundException, MalformedURLException {
        String directoryPath = getExternalFilesDir(null) + "/";
        String fileName = getNextReportFileName(directoryPath);
        String filePath = directoryPath + fileName;

        PdfWriter writer = new PdfWriter(filePath);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        try {
            document.add(new Paragraph("Event ratings report")
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.LEFT));
            document.add(new Paragraph("\n"));

            File chartImage = saveChartToImage(); 

            if (chartImage != null && chartImage.exists()) {
                Image chart = new Image(ImageDataFactory.create(chartImage.getAbsolutePath()));
                chart.scaleToFit(500, 500);
                document.add(chart);
                document.add(new Paragraph("\n"));
            }



            Table table = new Table(7);
            table.setWidth(UnitValue.createPercentValue(100));

            DeviceRgb headerColor = new DeviceRgb(44, 62, 80);
            DeviceRgb white = new DeviceRgb(255, 255, 255);
            String[] headers = {"Event name", "1", "2", "3", "4", "5", "Average rating"};

            for (String header : headers) {
                table.addCell(new Cell().add(new Paragraph(header))
                        .setBackgroundColor(headerColor)
                        .setFontColor(white)
                        .setBold()
                        .setTextAlignment(TextAlignment.LEFT));
            }

            for (EventRatingsDTO event : eventList) {
                table.addCell(new Cell().add(new Paragraph(event.getEventName()))
                        .setBackgroundColor(headerColor)
                        .setFontColor(white));
                for (int i = 1; i <= 5; i++) {
                    int count = event.getRatingCounts().getOrDefault(i, 0);
                    table.addCell(new Cell().add(new Paragraph(String.valueOf(count)))
                            .setTextAlignment(TextAlignment.LEFT));
                }
                table.addCell(new Cell().add(new Paragraph(String.format("%.2f", event.getAverageRating())))
                        .setTextAlignment(TextAlignment.LEFT));
            }
            document.add(table);
        } finally {
            document.close();
        }

        // open newly created pdf file
        openGeneratedPDF(filePath);
    }

    private String getNextReportFileName(String directoryPath) {
        File directory = new File(directoryPath);
        File[] files = directory.listFiles((dir, name) -> name.startsWith("Event_Ratings_Report") && name.endsWith(".pdf"));

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
        return "Event_Ratings_Report" + nextReportNumber + ".pdf";
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



    private File saveChartToImage() {
        barChart.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(barChart.getDrawingCache());
        barChart.setDrawingCacheEnabled(false);

        String fileName = "chart_" + System.currentTimeMillis() + ".png";  // generate unique name
        File chartFile = new File(getExternalFilesDir(null), fileName);

        try (FileOutputStream out = new FileOutputStream(chartFile)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return chartFile;
    }


}
