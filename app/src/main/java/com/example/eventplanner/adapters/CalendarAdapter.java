package com.example.eventplanner.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;

import java.util.HashMap;
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {
    private List<String> days;
    private HashMap<String, String> acceptedEvents;
    private HashMap<String, String> createdEvents;
    private int month, year;

    public CalendarAdapter(List<String> days, HashMap<String, String> acceptedEvents, int month, int year) {
        this.days = days;
        this.acceptedEvents = acceptedEvents;
        this.month = month;
        this.year = year;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String day = days.get(position);
        holder.dayText.setText(day);

        if (day.isEmpty()) {  
            holder.acceptedEventIndicator.setVisibility(View.GONE);
            holder.createdEventIndicator.setVisibility(View.GONE);
            holder.background.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.transparent));
            return;
        }

        String fullDate = year + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", Integer.parseInt(day));

        if (acceptedEvents.containsKey(fullDate)) {
            holder.acceptedEventIndicator.setVisibility(View.VISIBLE);
            holder.background.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.light_gray));
        } else {
            holder.acceptedEventIndicator.setVisibility(View.GONE);
        }

        /*
        if (createdEvents.containsKey(fullDate)) {
            holder.createdEventIndicator.setVisibility(View.VISIBLE);
        } else {
            holder.createdEventIndicator.setVisibility(View.GONE);
        }

         */
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.calendar_cell, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dayText;
        LinearLayout background;
        View acceptedEventIndicator;
        View createdEventIndicator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dayText = itemView.findViewById(R.id.dayText);
            background = itemView.findViewById(R.id.background);
            acceptedEventIndicator = itemView.findViewById(R.id.acceptedEventIndicator);
            createdEventIndicator = itemView.findViewById(R.id.createdEventIndicator);
        }
    }

    public void updateData(List<String> days, HashMap<String, String> acceptedEvents, int month, int year) {
        this.days = days;
        this.acceptedEvents = acceptedEvents;
        this.month = month;
        this.year = year;
        notifyDataSetChanged();
    }
}
