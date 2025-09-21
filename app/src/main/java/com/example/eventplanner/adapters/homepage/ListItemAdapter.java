package com.example.eventplanner.adapters.homepage;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventplanner.BuildConfig;
import com.example.eventplanner.R;
import com.example.eventplanner.activities.event.EventDetailsActivity;
import com.example.eventplanner.activities.homepage.CardItem;
import com.example.eventplanner.activities.product.ProductDetailsActivity;
import com.example.eventplanner.dto.event.GetEventDTO;
import com.example.eventplanner.dto.solution.GetHomepageSolutionDTO;
import com.google.android.material.imageview.ShapeableImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListItemAdapter extends RecyclerView.Adapter<ListItemAdapter.ViewHolder> {

    private final Context context;
    private final List<CardItem> items = new ArrayList<>();

    public ListItemAdapter(Context context) {
        this.context = context;
    }

    public void setItems(List<? extends CardItem> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CardItem item = items.get(position);

        holder.title.setText(item.getName() != null ? item.getName() : "");
        holder.description.setText(item.getDescription() != null ? item.getDescription() : "");

        String imgUrl = item.getImageUrl();
        if (imgUrl != null && !imgUrl.isEmpty()) {
            String fullImageUrl = imgUrl.startsWith("http") ? imgUrl : "http://" + BuildConfig.IP_ADDR + ":8080" + imgUrl;
            Glide.with(context)
                    .load(fullImageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.event1)
                    .error(R.drawable.event1)
                    .into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.event1);
        }

        if (item instanceof GetEventDTO) {
            GetEventDTO event = (GetEventDTO) item;

            holder.eventSpecificLayout.setVisibility(View.VISIBLE);
            holder.solutionSpecificLayout.setVisibility(View.GONE);

            holder.location.setText(event.getLocation() != null ? event.getLocation().getCity() : "");
            holder.category.setText(event.getEventTypeName() != null ? event.getEventTypeName() : "");
            holder.date.setText(event.getDate() != null ? new SimpleDateFormat("dd.MM.yyyy.", Locale.getDefault()).format(event.getDate()) : "");

        } else if (item instanceof GetHomepageSolutionDTO) {
            GetHomepageSolutionDTO solution = (GetHomepageSolutionDTO) item;

            holder.eventSpecificLayout.setVisibility(View.GONE);
            holder.solutionSpecificLayout.setVisibility(View.VISIBLE);

            holder.location.setText(solution.getCity() != null ? solution.getCity() : "");
            holder.category.setText(solution.getCategoryName() != null ? solution.getCategoryName() : "");

            holder.price.setText(String.format(Locale.getDefault(), "%.2f $", solution.getPrice()));

            if (solution.getDiscount() != null && solution.getDiscount() > 0) {
                holder.discountLayout.setVisibility(View.VISIBLE);
                holder.discount.setText(String.format(Locale.getDefault(), "%.0f%%", solution.getDiscount()));
            } else {
                holder.discountLayout.setVisibility(View.GONE);
            }
        }

        holder.itemView.setOnClickListener(v -> openDetails(item));
    }

    private void openDetails(CardItem item) {
        Intent i = null;
        if (item instanceof GetEventDTO) {
            i = new Intent(context, EventDetailsActivity.class);
            i.putExtra("id", item.getId());
        } else if (item instanceof GetHomepageSolutionDTO) {
            GetHomepageSolutionDTO solution = (GetHomepageSolutionDTO) item;
            if ("product".equalsIgnoreCase(solution.getType())) {
                i = new Intent(context, ProductDetailsActivity.class);
                i.putExtra("id", solution.getId());
            }
        }
        if (i != null) {
            context.startActivity(i);
        }
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView image;
        TextView title, description, location, category;
        LinearLayout eventSpecificLayout, solutionSpecificLayout, discountLayout;
        TextView date, price, discount, rating;
        ImageView locationIcon, categoryIcon, dateIcon, priceIcon, discountIcon;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.cardImage);
            title = itemView.findViewById(R.id.cardTitle);
            description = itemView.findViewById(R.id.cardDescription);
            location = itemView.findViewById(R.id.location);
            category = itemView.findViewById(R.id.category);

            eventSpecificLayout = itemView.findViewById(R.id.eventSpecificLayout);
            solutionSpecificLayout = itemView.findViewById(R.id.solutionSpecificLayout);

            date = itemView.findViewById(R.id.date);
            price = itemView.findViewById(R.id.price);
            discount = itemView.findViewById(R.id.discount);

            locationIcon = itemView.findViewById(R.id.locationIcon);
            categoryIcon = itemView.findViewById(R.id.categoryIcon);
            dateIcon = itemView.findViewById(R.id.dateIcon);
            priceIcon = itemView.findViewById(R.id.priceIcon);
            discountIcon = itemView.findViewById(R.id.discountIcon);
            discountLayout = itemView.findViewById(R.id.discountLayout);
        }
    }
}