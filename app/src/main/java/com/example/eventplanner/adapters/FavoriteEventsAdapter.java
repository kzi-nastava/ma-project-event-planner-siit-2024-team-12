package com.example.eventplanner.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.dto.event.FavEventDTO;

import java.util.List;

public class FavoriteEventsAdapter extends RecyclerView.Adapter<FavoriteEventsAdapter.ViewHolder> {
    private List<FavEventDTO> events;
    private Context context;

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

        if (position % 2 == 0) {
            holder.eventImage.setImageResource(R.drawable.event1);
            holder.container.removeAllViews();
            holder.container.addView(holder.eventImage);
            holder.container.addView(holder.textContainer);
        }
        else {
            holder.eventImage.setImageResource(R.drawable.event3);
            holder.container.removeAllViews();
            holder.container.addView(holder.textContainer);
            holder.container.addView(holder.eventImage);
        }


        holder.eventTitle.setText(event.getName());


        holder.seeMore.setOnClickListener(v -> {

        });
    }




    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView eventImage;
        TextView eventTitle, seeMore;
        LinearLayout container, textContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            container = (LinearLayout) itemView;
            eventImage = itemView.findViewById(R.id.eventImage);
            eventTitle = itemView.findViewById(R.id.eventTitle);
            seeMore = itemView.findViewById(R.id.seeMore);
            textContainer = (LinearLayout) itemView.findViewById(R.id.textContainer);
        }
    }


}
