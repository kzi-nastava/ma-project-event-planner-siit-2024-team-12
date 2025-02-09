package com.example.eventplanner.activities.charts;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventplanner.ClientUtils;
import com.example.eventplanner.R;
import com.example.eventplanner.dto.charts.EventAttendanceDTO;

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



public class AttendanceChart extends AppCompatActivity {

    private BarChart barChart;
    private ArrayList<String> eventNames = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_chart);


        barChart = findViewById(R.id.barChart);

        loadEventAttendance();

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
                    setBarChart(response.body());
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

}