package com.example.eventplanner.activities.charts;

import android.content.Intent;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;


public class AttendanceChart extends AppCompatActivity {

    private BarChart barChart;
    private ArrayList<String> eventNames = new ArrayList<>();
    private ArrayList<EventAttendanceDTO> eventList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_chart);

        barChart = findViewById(R.id.barChart);

        loadEventAttendance();

        Button pdfBtn = findViewById(R.id.pdfBtn);
        pdfBtn.setOnClickListener(v -> {
            if (!eventList.isEmpty()) {
                try {
                    generatePDFWithChartAndTable(eventList);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(AttendanceChart.this, "Error generating PDF!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(AttendanceChart.this, "Please wait, loading event data...", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void closeForm(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }


    private void loadEventAttendance() {
        String auth = ClientUtils.getAuthorization(this);

        Call<ArrayList<EventAttendanceDTO>> call = ClientUtils.chartService.getEventAttendance(auth);

        call.enqueue(new Callback<ArrayList<EventAttendanceDTO>>() {
            @Override
            public void onResponse(Call<ArrayList<EventAttendanceDTO>> call, Response<ArrayList<EventAttendanceDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    eventList = response.body();
                    setBarChart(eventList);
                } else {
                    Toast.makeText(AttendanceChart.this, "No data available!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<EventAttendanceDTO>> call, Throwable t) {
                Toast.makeText(AttendanceChart.this, "Failed to load event attendance!", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void setBarChart(ArrayList<EventAttendanceDTO> eventList) {
        ArrayList<BarEntry> stackedEntries = new ArrayList<>();
        eventNames.clear(); // avoid duplicate event names

        // set limit to 15 bars for better readability
        // other events will be included in detailed pdf report
        int limit = Math.min(eventList.size(), 15);

        for (int i = 0; i < limit; i++) {
            EventAttendanceDTO event = eventList.get(i);
            eventNames.add(event.getEventName());

            float maxGuests = event.getMaxGuests();
            float attendance = event.getAttendance();
            float percentage = event.getPercentage().floatValue();

            stackedEntries.add(new BarEntry(i, new float[]{maxGuests, attendance, percentage}));
        }

        BarDataSet stackedSet = new BarDataSet(stackedEntries, "");
        stackedSet.setColors(new int[]{Color.parseColor("#FF9999"), Color.parseColor("#66B3FF"), Color.parseColor("#C2BFFF")});
        stackedSet.setStackLabels(new String[]{"Max guests", "Attendance", "Percentage (%)"});

        BarData barData = new BarData(stackedSet);
        barData.setBarWidth(0.8f);
        barData.setValueTextSize(9f);

        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.setFitBars(true);
        barChart.invalidate();

        // configure x axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(eventNames));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(270f);
        xAxis.setTextSize(14f);
        xAxis.setDrawLabels(true);
        xAxis.setGranularityEnabled(true);
        xAxis.setAvoidFirstLastClipping(false);
        xAxis.setLabelCount(limit);

        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);

        barChart.setExtraBottomOffset(250f);
        barChart.getAxisRight().setEnabled(false);
        barChart.setExtraTopOffset(30f);

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


    private void generatePDFWithChartAndTable(ArrayList<EventAttendanceDTO> eventList) throws FileNotFoundException {
        String directoryPath = getExternalFilesDir(null) + "/";
        String fileName = getNextReportFileName(directoryPath);
        String filePath = directoryPath + fileName;

        PdfWriter writer = new PdfWriter(filePath);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        try {

            Paragraph title = new Paragraph("Event attendance report")
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.LEFT);

            document.add(title);
            document.add(new Paragraph("\n"));


            // add chart image
            File chartImage = new File(getExternalFilesDir(null), "chart.png");
            if (chartImage.exists()) {
                Image chart = new Image(ImageDataFactory.create(chartImage.getAbsolutePath()));
                chart.scaleToFit(500, 500);
                document.add(chart);
                document.add(new Paragraph("\n"));
            }

            // add detailed table
            Table table = new Table(4);
            table.setWidth(UnitValue.createPercentValue(100));

            // set header
            DeviceRgb headerColor = new DeviceRgb(44, 62, 80);
            DeviceRgb white = new DeviceRgb(255, 255, 255);
            table.addCell(new Cell().add(new Paragraph("Event name")).setBackgroundColor(headerColor).setFontColor(white));
            table.addCell(new Cell().add(new Paragraph("Max guests")).setBackgroundColor(headerColor).setFontColor(white));
            table.addCell(new Cell().add(new Paragraph("Attended")).setBackgroundColor(headerColor).setFontColor(white));
            table.addCell(new Cell().add(new Paragraph("Attendance %")).setBackgroundColor(headerColor).setFontColor(white));

            DeviceRgb firstColumnColor = new DeviceRgb(44, 62, 80);

            // populate table
            for (EventAttendanceDTO event : eventList) {
                table.addCell(new Cell().add(new Paragraph(event.getEventName())).setBackgroundColor(firstColumnColor).setFontColor(white));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(event.getMaxGuests()))));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(event.getAttendance()))));
                table.addCell(new Cell().add(new Paragraph(event.getPercentage() + " %")));
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

    // reports are stored in format Event_Attendance_ReportX , X = number of report
    private String getNextReportFileName(String directoryPath) {
        File directory = new File(directoryPath);
        File[] files = directory.listFiles((dir, name) -> name.startsWith("Event_Attendance_Report") && name.endsWith(".pdf"));

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
        return "Event_Attendance_Report" + nextReportNumber + ".pdf";
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

}