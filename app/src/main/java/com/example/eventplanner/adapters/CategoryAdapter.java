package com.example.eventplanner.adapters;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.viewholders.CategoryViewHolder;
import com.example.eventplanner.model.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryViewHolder> {

    private List<Category> categoryList;

    public CategoryAdapter(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_row, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);

        holder.idTextView.setText(category.getId());
        holder.nameTextView.setText(category.getName());

        if (category.isExpanded()) {
            holder.descriptionTextView.setVisibility(View.VISIBLE);
            holder.statusTextView.setVisibility(View.VISIBLE);

            holder.expandArrow.setRotation(180f);
        } else {
            holder.descriptionTextView.setVisibility(View.GONE);
            holder.statusTextView.setVisibility(View.GONE);

            holder.expandArrow.setRotation(0f);
        }

        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.even_row_color));
        } else {
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.odd_row_color));
        }


        holder.itemView.setOnClickListener(v -> {
            category.setExpanded(!category.isExpanded());
            notifyItemChanged(position);
        });
    }


    @Override
    public int getItemCount() {
        return categoryList.size();
    }


}

