package com.example.eventplanner.adapters.favorites;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.content.Intent;

import com.example.eventplanner.utils.ClientUtils;
import com.example.eventplanner.activities.event.EventDetailsActivity;

import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.dto.event.EventDetailsDTO;
import com.example.eventplanner.dto.event.FavEventDTO;

import java.io.Serializable;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteEventsAdapter extends RecyclerView.Adapter<FavoriteEventsAdapter.ViewHolder> {
    private List<FavEventDTO> events;

    public FavoriteEventsAdapter(List<FavEventDTO> events) {
        this.events = events;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fav_event, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FavEventDTO event = events.get(position);

        holder.eventTitle.setText(event.getName());
        holder.eventLocation.setText(event.getCity() + ", " + event.getCountry());
        holder.eventDate.setText(event.getStartDate().toString());
        if (event.getStartTime() == null) {
            holder.eventTime.setText("");
        }
        else {
            holder.eventTime.setText(event.getStartTime().toString());
        }

        if (position % 2 == 0) {
            holder.eventImage.setImageResource(R.drawable.event1);
            holder.container.removeAllViews();
            holder.container.addView(holder.eventImage);
            holder.container.addView(holder.textContainer);
        } else {
            holder.eventImage.setImageResource(R.drawable.event3);
            holder.container.removeAllViews();
            holder.container.addView(holder.textContainer);
            holder.container.addView(holder.eventImage);
        }

        holder.seeMore.setOnClickListener(v -> {
            Context context = v.getContext();
            loadEventDetails(context, event.getId());
        });
    }



    private void loadEventDetails(Context context, Long eventId) {
        String token = ClientUtils.getAuthorization(context);

        Call<EventDetailsDTO> call = ClientUtils.eventService.getEvent(token, eventId);

        call.enqueue(new Callback<EventDetailsDTO>() {
            @Override
            public void onResponse(Call<EventDetailsDTO> call, Response<EventDetailsDTO> response) {
                if (response.isSuccessful()) {
                    EventDetailsDTO event = response.body();

                    Intent intent = new Intent(context, EventDetailsActivity.class);
                    intent.putExtra("id", eventId);
                    intent.putExtra("name", event.getName());
                    intent.putExtra("eventType", event.getEventType());
                    intent.putExtra("date", event.getDate().toString());
                    intent.putExtra("maxGuests", event.getMaxGuests());
                    intent.putExtra("description", event.getDescription());
                    intent.putExtra("location", event.getLocation().getAddress() + ", " +
                            event.getLocation().getCity() + ", " + event.getLocation().getCountry());
                    intent.putExtra("activities", (Serializable) event.getActivities());
                    context.startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<EventDetailsDTO> call, Throwable t) {

            }
        });
    }



    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView eventImage;
        TextView eventTitle, seeMore, eventLocation, eventDate, eventTime;
        LinearLayout container, textContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            container = (LinearLayout) itemView;
            eventImage = itemView.findViewById(R.id.eventImage);
            eventTitle = itemView.findViewById(R.id.eventTitle);
            seeMore = itemView.findViewById(R.id.seeMore);
            textContainer = itemView.findViewById(R.id.textContainer);
            eventLocation = itemView.findViewById(R.id.eventLocation);
            eventDate = itemView.findViewById(R.id.eventDate);
            eventTime = itemView.findViewById(R.id.eventTime);
        }
    }

}
