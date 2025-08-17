package com.example.eventplanner.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.utils.ClientUtils;
import com.example.eventplanner.R;
import com.example.eventplanner.activities.event.EventDetailsActivity;
import com.example.eventplanner.dto.event.EventDetailsDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {
    private List<String> days;
    private HashMap<String, List<String>> acceptedEvents;
    private HashMap<String, List<String>> createdEvents;
    private int month, year;


    public CalendarAdapter(List<String> days, HashMap<String, List<String>> acceptedEvents,
                           HashMap<String, List<String>> createdEvents, int month, int year) {
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

        List<String> acceptedEventList = acceptedEvents.getOrDefault(date, new ArrayList<>());
        List<String> createdEventList = createdEvents.getOrDefault(date, new ArrayList<>());

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_events, null);

        TextView acceptedEventsList = dialogView.findViewById(R.id.acceptedEventsList);
        TextView createdEventsList = dialogView.findViewById(R.id.createdEventsList);

        String acceptedEventsText = acceptedEventList.isEmpty() ? noEvents : String.join("\n", acceptedEventList);
        String createdEventsText = createdEventList.isEmpty() ? noEvents : String.join("\n", createdEventList);

        acceptedEventsList.setText(acceptedEventsText);
        createdEventsList.setText(createdEventsText);

        acceptedEventsList.setOnClickListener(v -> {
            showEventSelectionDialog(context, "Select event to view details:", acceptedEventList);
        });

        createdEventsList.setOnClickListener(v -> {
            showEventSelectionDialog(context, "Select event to view details:", createdEventList);
        });

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


    private void showEventSelectionDialog(Context context, String title, List<String> eventList) {
        String[] eventArray = eventList.toArray(new String[0]);

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setItems(eventArray, (dialog, which) -> {
                    String selectedEvent = eventArray[which];
                    findByName(context, selectedEvent);
                })
                .show();
    }


    private void findByName(Context context, String eventName) {
        String auth = ClientUtils.getAuthorization(context);

        Call<EventDetailsDTO> call = ClientUtils.eventService.findByName(auth, eventName);
        call.enqueue(new Callback<EventDetailsDTO>() {
            @Override
            public void onResponse(Call<EventDetailsDTO> call, Response<EventDetailsDTO> response) {
                if (response.isSuccessful()) {
                    EventDetailsDTO event = response.body();
                    Intent intent = new Intent(context, EventDetailsActivity.class);
                    intent.putExtra("id", event.getId());
                    context.startActivity(intent);
                }
                else {
                    Toast.makeText(context, "Error loading event details!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EventDetailsDTO> call, Throwable t) {
                Toast.makeText(context, "Failed to load event details!", Toast.LENGTH_SHORT).show();
            }
        });
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

    public void updateData(List<String> days, HashMap<String, List<String>> acceptedEvents, HashMap<String, List<String>> createdEvents, int month, int year) {
        this.days = days;
        this.acceptedEvents = acceptedEvents;
        this.createdEvents = createdEvents;
        this.month = month;
        this.year = year;
        notifyDataSetChanged();
    }

}
