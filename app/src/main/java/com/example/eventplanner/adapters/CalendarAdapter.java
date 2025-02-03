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
    private HashMap<String, String> events;
    private int month, year;

    public CalendarAdapter(List<String> days, HashMap<String, String> events, int month, int year) {
        this.days = days;
        this.events = events;
        this.month = month;
        this.year = year;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String day = days.get(position);
        holder.dayText.setText(day);

        if (!day.isEmpty()) {
            String fullDate = year + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", Integer.parseInt(day));

            if (events.containsKey(fullDate)) {
                holder.eventText.setText(events.get(fullDate));
                holder.eventText.setVisibility(View.VISIBLE);
                holder.background.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.light_gray));

                // set color for dates with events
                holder.dayText.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.pink));
                holder.dayText.setTypeface(null, android.graphics.Typeface.BOLD);
            } else {
                holder.eventText.setVisibility(View.GONE);
                holder.background.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.transparent));

                // reset if there is no events
                holder.dayText.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.black));
                holder.dayText.setTypeface(null, android.graphics.Typeface.NORMAL);
            }
        }
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
        TextView eventText;
        LinearLayout background;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dayText = itemView.findViewById(R.id.dayText);
            eventText = itemView.findViewById(R.id.eventText);
            background = itemView.findViewById(R.id.background);
        }
    }

    public void updateData(List<String> days, HashMap<String, String> events, int month, int year) {
        this.days = days;
        this.events = events;
        this.month = month;
        this.year = year;
        notifyDataSetChanged();
    }
}
