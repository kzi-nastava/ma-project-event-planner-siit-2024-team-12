package com.example.eventplanner.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

    public CalendarAdapter(List<String> days, HashMap<String, String> acceptedEvents,
                           HashMap<String, String> createdEvents, int month, int year) {
        this.days = days;
        this.acceptedEvents = acceptedEvents;
        this.createdEvents = createdEvents;
        this.month = month;
        this.year = year;
    }




    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String day = days.get(position);
        holder.dayText.setText(day);

        if (day.isEmpty()) {
            holder.eventIndicatorsContainer.removeAllViews();
            holder.background.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.transparent));
            return;
        }

        String fullDate = year + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", Integer.parseInt(day));

        // reset before adding new indicators
        holder.eventIndicatorsContainer.removeAllViews();

        int marginBetweenIndicators = 5;
        int marginInPx = (int) (marginBetweenIndicators * holder.itemView.getContext().getResources().getDisplayMetrics().density);

        // add accepted event indicator
        if (acceptedEvents.containsKey(fullDate)) {
            View acceptedIndicator = new View(holder.itemView.getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 15);
            params.setMargins(0, 0, 0, marginInPx);
            acceptedIndicator.setLayoutParams(params);
            acceptedIndicator.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
            holder.eventIndicatorsContainer.addView(acceptedIndicator);
        }

        // add created event indicator
        if (createdEvents.containsKey(fullDate)) {
            View createdIndicator = new View(holder.itemView.getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 15);
            params.setMargins(0, 0, 0, marginInPx);
            createdIndicator.setLayoutParams(params);
            createdIndicator.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.pink));
            holder.eventIndicatorsContainer.addView(createdIndicator);
        }

        // open dialog when date is clicked
        holder.itemView.setOnClickListener(v -> showEventDialog(holder.itemView.getContext(), fullDate));
    }


    private void showEventDialog(Context context, String date) {
        String events = context.getString(R.string.events_title);
        String noEvents = context.getString(R.string.no_events);
        String close = context.getString(R.string.close);

        String acceptedEvent = acceptedEvents.getOrDefault(date, noEvents);
        String createdEvent = createdEvents.getOrDefault(date, noEvents);

        // set up the dialog layout
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_events, null);

        // find the TextViews where the events will be displayed
        TextView acceptedEventsList = dialogView.findViewById(R.id.acceptedEventsList);
        TextView createdEventsList = dialogView.findViewById(R.id.createdEventsList);

        // set the events in the respective columns
        acceptedEventsList.setText(acceptedEvent);
        createdEventsList.setText(createdEvent);

        // create and show the dialog
        TextView dialogTitle = new TextView(context);
        dialogTitle.setText(date + "   " + events);
        dialogTitle.setTextSize(22);
        dialogTitle.setTextColor(ContextCompat.getColor(context, R.color.black));
        dialogTitle.setPadding(50, 30, 30, 30);
        dialogTitle.setTypeface(null, Typeface.BOLD);

        new AlertDialog.Builder(context)
                .setCustomTitle(dialogTitle)
                .setView(dialogView)
                .setPositiveButton(close, (dialog, which) -> dialog.dismiss())
                .show();
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
        LinearLayout eventIndicatorsContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dayText = itemView.findViewById(R.id.dayText);
            background = itemView.findViewById(R.id.background);
            eventIndicatorsContainer = itemView.findViewById(R.id.eventIndicatorsContainer);
        }
    }


    public void updateData(List<String> days, HashMap<String, String> acceptedEvents,
                           HashMap<String, String> createdEvents, int month, int year) {
        this.days = days;
        this.acceptedEvents = acceptedEvents;
        this.createdEvents = createdEvents;
        this.month = month;
        this.year = year;
        notifyDataSetChanged();
    }
}
