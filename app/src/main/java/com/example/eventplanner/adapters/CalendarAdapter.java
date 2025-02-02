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

    private HashMap<Integer, String> events;

    public CalendarAdapter(List<String> days, HashMap<Integer, String> events) {
        this.days = days;
        this.events = events;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.dayText.setText(days.get(position));

        if (!days.get(position).isEmpty() && events.containsKey(Integer.parseInt(days.get(position)))) {
            holder.eventText.setText(events.get(Integer.parseInt(days.get(position))));
            holder.eventText.setVisibility(View.VISIBLE);
            holder.background.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.light_gray));
        } else {
            holder.eventText.setVisibility(View.GONE);
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


    public void updateData(List<String> days, HashMap<Integer, String> events) {
        this.days = days;
        this.events = events;
        notifyDataSetChanged();
    }


}
