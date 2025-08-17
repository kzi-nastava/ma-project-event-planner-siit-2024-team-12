package com.example.eventplanner.adapters.event;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.viewholders.AgendaViewHolder;
import com.example.eventplanner.dto.agenda.CreateActivityDTO;
import com.example.eventplanner.fragments.eventcreation.ActivityFormFragment;
import com.example.eventplanner.model.Activity;
import com.example.eventplanner.viewmodels.EventEditViewModel;

import java.util.List;

public class AgendaAdapter extends RecyclerView.Adapter<AgendaViewHolder> {

    private List<Activity> activities;
    private Boolean isEditable;
    private EventEditViewModel viewModel;

    public AgendaAdapter(List<Activity> activities, Boolean isEditable) {
        this.activities = activities;
        this.isEditable = isEditable;
    }



    @NonNull
    @Override
    public AgendaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_agenda_row, parent, false);

        Context context = parent.getContext();
        if (context instanceof FragmentActivity) {
            FragmentActivity activity = (FragmentActivity) context;
            viewModel = new ViewModelProvider(activity).get(EventEditViewModel.class);
        }


        return new AgendaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AgendaViewHolder holder, int position) {
        Activity activity = activities.get(position);

        holder.activityTime.setText(activity.getTime());

        if (activity.isExpanded()) {
            holder.activityName.setVisibility(View.VISIBLE);
            holder.activityDescription.setVisibility(View.VISIBLE);
            holder.activityVenue.setVisibility(View.VISIBLE);

            if (isEditable) {
                CreateActivityDTO activityDTO = new CreateActivityDTO(activity.getTime(),
                        activity.getName(), activity.getDescription(), activity.getLocation());

                holder.buttonLayout.setVisibility(View.VISIBLE);

                holder.editButton.setVisibility(View.VISIBLE);
                holder.editButton.setOnClickListener(v -> {
                    ActivityFormFragment activityForm = ActivityFormFragment.newEditInstance(true, activityDTO, position);
                    activityForm.show(((FragmentActivity) v.getContext()).getSupportFragmentManager(), "ActivityForm");
                });

                holder.deleteButton.setVisibility(View.VISIBLE);
                holder.deleteButton.setOnClickListener(v -> {
                    viewModel.deleteActivity(activityDTO);
                });


            }

            // bold "Activity name*" part in table
            String name = holder.itemView.getContext().getString(R.string.activity_name_in_table, activity.getName());
            SpannableString spannable = new SpannableString(name);
            spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, 11, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.activityName.setText(spannable);

            String description = holder.itemView.getContext().getString(R.string.description_in_table_row, activity.getDescription());
            SpannableString spannable2 = new SpannableString(description);
            spannable2.setSpan(new StyleSpan(Typeface.BOLD), 0, 11, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.activityDescription.setText(spannable2);

            String venue = holder.itemView.getContext().getString(R.string.location_in_table, activity.getLocation());
            SpannableString spannable3 = new SpannableString(venue);
            spannable3.setSpan(new StyleSpan(Typeface.BOLD), 0, 11, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.activityVenue.setText(spannable3);

            holder.expandArrow.setRotation(180f);
        } else {
            holder.activityName.setVisibility(View.GONE);
            holder.activityDescription.setVisibility(View.GONE);
            holder.activityVenue.setVisibility(View.GONE);

            holder.buttonLayout.setVisibility(View.GONE);
            holder.editButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);

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
        return activities.size();
    }
}
