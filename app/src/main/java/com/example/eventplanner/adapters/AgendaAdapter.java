package com.example.eventplanner.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.viewholders.AgendaViewHolder;
import com.example.eventplanner.model.Activity;

import java.util.List;

public class AgendaAdapter extends RecyclerView.Adapter<AgendaViewHolder> {

    private List<Activity> activites;

    public AgendaAdapter(List<Activity> activites) {
        this.activites = activites;
    }

    @NonNull
    @Override
    public AgendaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_agenda_row, parent, false);

        return new AgendaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AgendaViewHolder holder, int position) {
        Activity activity = activites.get(position);

        holder.activityTime.setText(activity.getTime());

        if (activity.isExpanded()) {
            holder.activityName.setVisibility(View.VISIBLE);
            holder.activityDescription.setVisibility(View.VISIBLE);
            holder.activityVenue.setVisibility(View.VISIBLE);

            holder.activityName.setText(activity.getName());
            holder.activityDescription.setText(activity.getDescription());
            holder.activityVenue.setText(activity.getLocation());

            holder.expandArrow.setRotation(180f);
        } else {
            holder.activityName.setVisibility(View.GONE);
            holder.activityDescription.setVisibility(View.GONE);
            holder.activityVenue.setVisibility(View.GONE);

            holder.expandArrow.setRotation(0f);
        }


        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.even_row_color));
        }
        else {
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.odd_row_color));
        }

        holder.itemView.setOnClickListener(v -> {
            activity.setExpanded(!activity.isExpanded());
            Log.d("AgendaAdapter", "Activity expanded: " + activity.isExpanded());
            notifyItemChanged(position);
        });

    }

    @Override
    public int getItemCount() {
        return activites.size();
    }
}
